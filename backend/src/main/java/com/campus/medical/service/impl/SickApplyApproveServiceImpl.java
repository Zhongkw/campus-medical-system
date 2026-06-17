package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalSickLeaveMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.SickApplyApproveService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 病假申请审批服务实现类
 */
@Slf4j
@Service
public class SickApplyApproveServiceImpl implements SickApplyApproveService {
    
    @Autowired
    private MedicalSickLeaveMapper medicalSickLeaveMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Override
    public PageResultVO<SickApplyVO> pageQuerySickApply(SickApplyQueryDTO queryDTO) {
        Page<MedicalSickLeave> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getAuditStatus() != null) {
            wrapper.eq(MedicalSickLeave::getStatus, queryDTO.getAuditStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(MedicalSickLeave::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(MedicalSickLeave::getCreateTime, queryDTO.getEndTime());
        }
        
        wrapper.orderByDesc(MedicalSickLeave::getCreateTime);
        
        IPage<MedicalSickLeave> leavePage = medicalSickLeaveMapper.selectPage(page, wrapper);
        
        List<SickApplyVO> voList = leavePage.getRecords().stream().map(leave -> {
            SysUser user = sysUserMapper.selectById(leave.getUserId());
            if (queryDTO.getCollege() != null && !queryDTO.getCollege().isEmpty()) {
                if (user == null || user.getDepartment() == null
                        || !queryDTO.getCollege().equals(user.getDepartment())) {
                    return null;
                }
            }
            SickApplyVO vo = new SickApplyVO();
            vo.setId(leave.getId());
            vo.setLeaveNo(leave.getLeaveNo());
            vo.setStudentName(user != null ? user.getRealName() : "未知");
            vo.setCollege(user != null ? user.getDepartment() : "");
            vo.setDays(leave.getLeaveDays());
            vo.setStatus(leave.getStatus());
            vo.setStatusName(getStatusName(String.valueOf(leave.getStatus())));
            vo.setCreateTime(leave.getCreateTime());
            return vo;
        }).filter(vo -> vo != null).collect(Collectors.toList());
        
        return new PageResultVO<>(voList, leavePage.getTotal(), leavePage.getPages(),
            leavePage.getCurrent(), leavePage.getSize());
    }
    
    @Override
    public SickApplyDetailVO getApplyDetail(Long applyId) {
        MedicalSickLeave leave = medicalSickLeaveMapper.selectById(applyId);
        if (leave == null) {
            return null;
        }
        
        SickApplyDetailVO vo = new SickApplyDetailVO();
        vo.setId(leave.getId());
        vo.setLeaveNo(leave.getLeaveNo());
        // 获取关联用户信息
        SysUser user = sysUserMapper.selectById(leave.getUserId());
        vo.setStudentName(user != null ? user.getRealName() : "未知");
        vo.setStudentNo(user != null ? user.getUsername() : "未知");
        vo.setCollege(user != null ? user.getDepartment() : "");
        vo.setClassName(user != null ? user.getClassName() : "未知");
        vo.setPhone(user != null ? user.getPhone() : "未知");
        vo.setStartDate(leave.getStartDate().toString());
        vo.setEndDate(leave.getEndDate().toString());
        vo.setDays(leave.getLeaveDays());
        vo.setReason(leave.getDiagnosis());
        vo.setDiagnosisProofUrl(leave.getDiagnosisProofUrl());
        vo.setStatus(leave.getStatus());
        vo.setStatusName(getStatusName(String.valueOf(leave.getStatus())));
        vo.setAuditContent(leave.getAuditContent());
        vo.setAuditTime(leave.getAuditTime());
        vo.setCreateTime(leave.getCreateTime());
        
        // 获取审核人姓名
        if (leave.getAuditorId() != null) {
            SysUser auditor = sysUserMapper.selectById(leave.getAuditorId());
            if (auditor != null) {
                vo.setAuditorName(auditor.getRealName());
            }
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doSickApplyAudit(Long applyId, String auditStatus, String auditContent, Long auditorId) {
        MedicalSickLeave leave = medicalSickLeaveMapper.selectById(applyId);
        if (leave == null) {
            return false;
        }
        
        // 根据传入的状态字符串设置对应的状态值
        if("PASS".equals(auditStatus)) {
            leave.setStatus(2); // 已通过
        } else if("REJECT".equals(auditStatus)) {
            leave.setStatus(3); // 已驳回
        } else {
            // 将状态字符串转换为对应的整数值
            if ("待审核".equals(auditStatus)) {
                leave.setStatus(1);
            } else if ("已通过".equals(auditStatus)) {
                leave.setStatus(2);
            } else if ("已驳回".equals(auditStatus)) {
                leave.setStatus(3);
            } else if ("已撤回".equals(auditStatus)) {
                leave.setStatus(4);
            } else {
                // 默认为待审核
                leave.setStatus(1);
            }
        }
        leave.setAuditContent(auditContent);
        leave.setAuditTime(LocalDateTime.now());
        leave.setUpdateTime(LocalDateTime.now());
        leave.setAuditorId(auditorId);
        
        return medicalSickLeaveMapper.updateById(leave) > 0;
    }
    
    @Override
    public List<CollegeSickStatVO> getCollegeSickStatData() {
        List<MedicalSickLeave> leaves = medicalSickLeaveMapper.selectList(null);
        
        Map<String, Long> statMap = new HashMap<>();
        for (MedicalSickLeave leave : leaves) {
            SysUser user = sysUserMapper.selectById(leave.getUserId());
            String college = (user != null && user.getDepartment() != null) ? user.getDepartment() : "未知";
            statMap.merge(college, 1L, Long::sum);
        }
        
        return statMap.entrySet().stream().map(entry -> {
            CollegeSickStatVO vo = new CollegeSickStatVO();
            vo.setCollege(entry.getKey());
            vo.setCount(entry.getValue());
            return vo;
        }).collect(Collectors.toList());
    }
    
    private String getStatusName(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "待审核": return "待审核";
            case "已通过": return "已通过";
            case "已驳回": return "已驳回";
            case "已撤回": return "已撤回";
            default: return status;
        }
    }
}