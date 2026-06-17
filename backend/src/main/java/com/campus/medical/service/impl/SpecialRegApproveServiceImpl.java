package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalApprovalMapper;
import com.campus.medical.mapper.MedicalAppointmentMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.SpecialRegApproveService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 特殊挂号审批服务实现类
 */
@Slf4j
@Service
public class SpecialRegApproveServiceImpl implements SpecialRegApproveService {
    
    @Autowired
    private MedicalApprovalMapper medicalApprovalMapper;
    
    @Autowired
    private MedicalAppointmentMapper medicalAppointmentMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public PageResultVO<SpecialRegVO> querySpecialRegList(SickApplyQueryDTO queryDTO) {
        Page<MedicalApproval> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MedicalApproval::getApprovalType, "特殊挂号");

        if (queryDTO.getAuditStatus() != null) {
            wrapper.eq(MedicalApproval::getStatus, queryDTO.getAuditStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(MedicalApproval::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(MedicalApproval::getCreateTime, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(MedicalApproval::getCreateTime);

        IPage<MedicalApproval> approvalPage = medicalApprovalMapper.selectPage(page, wrapper);

        List<SpecialRegVO> voList = approvalPage.getRecords().stream().map(approval -> {
            SpecialRegVO vo = new SpecialRegVO();
            vo.setId(approval.getId());
            SysUser user = sysUserMapper.selectById(approval.getApplicantId());
            vo.setStudentName(user != null ? user.getRealName() : "未知");
            vo.setDoctorName("未知");
            vo.setTimeSlot("未知");
            vo.setStatus(convertStatusToInt(approval.getStatus()));
            vo.setStatusName(getStatusName(approval.getStatus()));
            vo.setCreateTime(approval.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        return new PageResultVO<>(voList, approvalPage.getTotal(), approvalPage.getPages(),
                approvalPage.getCurrent(), approvalPage.getSize());
    }

    @Override
    public SpecialRegDetailVO getRegDetail(Long regId) {
        MedicalApproval approval = medicalApprovalMapper.selectById(regId);
        if (approval == null) {
            return null;
        }

        SpecialRegDetailVO vo = new SpecialRegDetailVO();
        vo.setId(approval.getId());
        SysUser user = sysUserMapper.selectById(approval.getApplicantId());
        vo.setStudentName(user != null ? user.getRealName() : "未知");
        vo.setStudentNo(user != null ? user.getUsername() : "未知");
        vo.setPhone(user != null ? user.getPhone() : "未知");
        vo.setDoctorName("未知");
        vo.setDeptName("未知");
        vo.setVisitDate("未知");
        vo.setTimeSlot("未知");
        vo.setApplyReason(approval.getReason());
        vo.setStatus(convertStatusToInt(approval.getStatus()));
        vo.setStatusName(getStatusName(approval.getStatus()));
        vo.setAuditRemark(approval.getAuditContent());
        vo.setAuditTime(approval.getAuditTime());
        vo.setCreateTime(approval.getCreateTime());

        if (approval.getAuditorId() != null) {
            SysUser auditor = sysUserMapper.selectById(approval.getAuditorId());
            if (auditor != null) {
                vo.setAuditorName(auditor.getRealName());
            }
        }

        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSpecialReg(Long regId, String auditStatus, String remark, Long auditorId) {
        MedicalApproval approval = medicalApprovalMapper.selectById(regId);
        if (approval == null) {
            return false;
        }
        
        approval.setStatus(auditStatus);
        approval.setAuditContent(remark);
        approval.setUpdateTime(LocalDateTime.now()); // 使用update_time而不是audit_time
        // approval.setAuditorId(auditorId); // 如果字段不存在则跳过
        
        boolean result = medicalApprovalMapper.updateById(approval) > 0;
        
        // 如果审核通过，更新预约状态
        if (result && "已通过".equals(approval.getStatus()) && approval.getBusinessId() != null) {
            MedicalAppointment appointment = medicalAppointmentMapper.selectById(approval.getBusinessId());
            if (appointment != null) {
                appointment.setStatus("已确认"); // 已确认
                medicalAppointmentMapper.updateById(appointment);
            }
        }
        
        return result;
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

    private Integer convertStatusToInt(String status) {
        if (status == null) return null;
        switch (status) {
            case "待审核": return 0;
            case "已通过": return 1;
            case "已驳回": return 2;
            case "已撤回": return 3;
            default: return null;
        }
    }
}
