package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.LoginDTO;
import com.campus.medical.dto.LoginVO;
import com.campus.medical.dto.UserInfoVO;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.AuthService;
import com.campus.medical.utils.JwtUtils;
import com.campus.medical.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        // 2. 验证用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 4. 验证密码（支持BCrypt、MD5和明文）
        boolean passwordMatch = false;
        if (PasswordUtil.isBCryptFormat(user.getPassword())) {
            // BCrypt格式密码
            passwordMatch = PasswordUtil.matches(loginDTO.getPassword(), user.getPassword());
        } else if (user.getPassword().length() == 32) {
            // 兼容旧版MD5密码（32位）
            String md5Password = org.springframework.util.DigestUtils.md5DigestAsHex(
                    loginDTO.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            passwordMatch = md5Password.equals(user.getPassword());

            // 兼容管理员模块创建用户时的加盐MD5
            if (!passwordMatch) {
                String saltedMd5 = org.springframework.util.DigestUtils.md5DigestAsHex(
                        ("CampusMedical" + loginDTO.getPassword() + "2026")
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );
                passwordMatch = saltedMd5.equals(user.getPassword());
            }

            // 如果是MD5密码且验证成功，自动升级为BCrypt
            if (passwordMatch) {
                upgradePasswordToBCrypt(user.getId(), loginDTO.getPassword());
            }
        } else {
            // 兼容明文密码（测试环境使用）
            passwordMatch = loginDTO.getPassword().equals(user.getPassword());
            
            // 如果是明文密码且验证成功，自动升级为BCrypt
            if (passwordMatch) {
                upgradePasswordToBCrypt(user.getId(), loginDTO.getPassword());
            }
        }

        if (!passwordMatch) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 5. 查询用户角色
        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 6. 生成JWT Token
        String accessToken = jwtUtils.generateToken(String.valueOf(user.getId()), role.getRoleCode());
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(user.getId()), role.getRoleCode());

        // 7. 更新最后登录时间和IP
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(getClientIp());
        userMapper.updateById(user);

        // 8. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setRoleCode(role.getRoleCode());
        loginVO.setRoleName(role.getRoleName());
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);

        log.info("用户登录成功：{}", user.getUsername());
        return loginVO;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        log.info("用户登出");
    }

    /**
     * 刷新Token
     */
    @Override
    public String refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        String userId = jwtUtils.getUserIdFromToken(refreshToken);
        String role = jwtUtils.getUserRoleFromToken(refreshToken);

        return jwtUtils.generateToken(userId, role);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        return "127.0.0.1";
    }

    /**
     * 将MD5密码升级为BCrypt
     */
    private void upgradePasswordToBCrypt(Long userId, String rawPassword) {
        try {
            String bcryptPassword = PasswordUtil.encode(rawPassword);
            SysUser user = new SysUser();
            user.setId(userId);
            user.setPassword(bcryptPassword);
            userMapper.updateById(user);
            log.info("用户 {} 的密码已从MD5升级为BCrypt", userId);
        } catch (Exception e) {
            log.error("密码升级失败：{}", e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setRoleCode(role.getRoleCode());
        userInfoVO.setRoleName(role.getRoleName());
        userInfoVO.setDepartment(user.getDepartment());
        userInfoVO.setClassName(user.getClassName());
        userInfoVO.setPhone(user.getPhone());

        return userInfoVO;
    }
}
