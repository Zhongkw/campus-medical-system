package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.medical.dto.SysUserDTO;
import com.campus.medical.dto.UserQueryDTO;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalDoctorMapper;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.SysUserService;
import com.campus.medical.utils.PasswordUtil;
import com.campus.medical.vo.PageResultVO;
import com.campus.medical.vo.SysUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;
    
    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return this.getOne(wrapper);
    }
    
    @Override
    public PageResultVO<SysUserVO> pageQuerySysUser(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
            wrapper.like(SysUser::getUsername, queryDTO.getUsername());
        }
        if (queryDTO.getRealName() != null && !queryDTO.getRealName().isEmpty()) {
            wrapper.like(SysUser::getRealName, queryDTO.getRealName());
        }
        if (queryDTO.getRoleId() != null) {
            wrapper.eq(SysUser::getRoleId, queryDTO.getRoleId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> userPage = this.page(page, wrapper);
        
        List<SysUserVO> voList = userPage.getRecords().stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            vo.setStatusName(user.getStatus() == 1 ? "启用" : "禁用");
            
            // 获取角色名称
            if (user.getRoleId() != null) {
                SysRole role = sysRoleMapper.selectById(user.getRoleId());
                if (role != null) {
                    vo.setRoleName(role.getRoleName());
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResultVO<>(voList, userPage.getTotal(), userPage.getPages(),
            userPage.getCurrent(), userPage.getSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addSystemUser(SysUserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (getByUsername(userDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        String rawPassword = (userDTO.getPassword() != null && !userDTO.getPassword().isBlank())
            ? userDTO.getPassword() : "123456";

        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(encryptPassword(rawPassword));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        boolean saved = this.save(user);
        if (saved) {
            createRoleProfileIfNeeded(user, userDTO);
        }
        return saved;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editSystemUser(SysUserDTO userDTO) {
        SysUser user = this.getById(userDTO.getId());
        if (user == null) {
            return false;
        }
        
        user.setRealName(userDTO.getRealName());
        user.setRoleId(userDTO.getRoleId());
        user.setDepartment(userDTO.getDepartment());
        user.setClassName(userDTO.getClassName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setUpdateTime(LocalDateTime.now());

        boolean updated = this.updateById(user);
        if (updated) {
            createRoleProfileIfNeeded(user, userDTO);
        }
        return updated;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean switchUserStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) {
            return false;
        }
        
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, userId)
            .set(SysUser::getStatus, status)
            .set(SysUser::getUpdateTime, LocalDateTime.now());
        
        return this.update(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetUserPwd(Long userId) {
        String newPwd = UUID.randomUUID().toString().substring(0, 8);
        String encryptedPwd = encryptPassword(newPwd);
        
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, userId)
            .set(SysUser::getPassword, encryptedPwd)
            .set(SysUser::getUpdateTime, LocalDateTime.now());
        
        if (this.update(wrapper)) {
            return newPwd;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSystemUser(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if ("admin".equals(getRoleCode(user.getRoleId()))) {
            Long adminCount = this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getRoleId, user.getRoleId())
                    .eq(SysUser::getStatus, 1));
            if (adminCount <= 1) {
                throw new RuntimeException("至少保留一名启用的系统管理员");
            }
        }
        doctorMapper.delete(new LambdaQueryWrapper<MedicalDoctor>()
                .eq(MedicalDoctor::getUserId, userId));
        return this.removeById(userId);
    }

    private String getRoleCode(Long roleId) {
        if (roleId == null) {
            return null;
        }
        SysRole role = sysRoleMapper.selectById(roleId);
        return role != null ? role.getRoleCode() : null;
    }
    
    private String encryptPassword(String password) {
        return PasswordUtil.encode(password);
    }

    private void createRoleProfileIfNeeded(SysUser user, SysUserDTO userDTO) {
        if (user.getRoleId() == null) {
            return;
        }
        SysRole role = sysRoleMapper.selectById(user.getRoleId());
        if (role == null || !"doctor".equals(role.getRoleCode())) {
            return;
        }
        if (userDTO.getDeptId() == null) {
            throw new RuntimeException("医生用户必须选择所属科室");
        }
        MedicalDepartment department = departmentMapper.selectById(userDTO.getDeptId());
        if (department == null) {
            throw new RuntimeException("所选科室不存在");
        }
        user.setDepartment(department.getDeptName());
        this.updateById(user);

        LambdaQueryWrapper<MedicalDoctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDoctor::getUserId, user.getId());
        MedicalDoctor doctor = doctorMapper.selectOne(wrapper);
        if (doctor == null) {
            doctor = new MedicalDoctor();
            doctor.setUserId(user.getId());
            doctor.setCreateTime(LocalDateTime.now());
        }
        doctor.setDeptId(userDTO.getDeptId());
        doctor.setTitle("医师");
        doctor.setSpecialty(department.getDeptName() + "诊疗");
        doctor.setIntroduction(user.getRealName() + "，" + department.getDeptName() + "医生");
        doctor.setUpdateTime(LocalDateTime.now());
        if (doctor.getId() == null) {
            doctorMapper.insert(doctor);
            log.info("创建医生档案: userId={}, deptId={}", user.getId(), userDTO.getDeptId());
        } else {
            doctorMapper.updateById(doctor);
            log.info("更新医生档案: userId={}, deptId={}", user.getId(), userDTO.getDeptId());
        }
    }
}
