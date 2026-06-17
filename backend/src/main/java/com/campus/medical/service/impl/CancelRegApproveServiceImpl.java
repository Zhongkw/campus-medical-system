package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.entity.*;
import com.campus.medical.mapper.*;
import com.campus.medical.service.AppointmentBusinessService;
import com.campus.medical.service.CancelRegApproveService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 退号申请审批服务实现类
 */
@Slf4j
@Service
public class CancelRegApproveServiceImpl implements CancelRegApproveService {

    private static final BigDecimal DEFAULT_REGISTER_FEE = new BigDecimal("10.00");
    private static final List<String> CANCEL_REG_TYPES = Arrays.asList("退号", "cancel_reg");

    @Autowired
    private MedicalApprovalMapper medicalApprovalMapper;

    @Autowired
    private MedicalAppointmentMapper appointmentMapper;

    @Autowired
    private MedicalQueueMapper queueMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AppointmentBusinessService appointmentBusinessService;

    @Override
    public List<StudentAppointmentVO> getStudentAppointments(Long userId) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getUserId, userId);
        wrapper.in(MedicalAppointment::getStatus, "待确认", "已确认", "待审批");
        wrapper.orderByDesc(MedicalAppointment::getCreateTime);
        List<MedicalAppointment> appointments = appointmentMapper.selectList(wrapper);

        Set<Long> pendingAppointmentIds = getPendingCancelAppointmentIds(userId);

        return appointments.stream().map(appointment -> {
            StudentAppointmentVO vo = new StudentAppointmentVO();
            vo.setId(appointment.getId());
            vo.setAppointmentNo(appointment.getAppointmentNo());
            vo.setAppointmentDate(appointment.getAppointmentDate() != null
                    ? appointment.getAppointmentDate().toString() : "");
            vo.setTimeSlot(appointment.getTimeSlot());
            vo.setStatus(appointment.getStatus());

            MedicalDoctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor != null) {
                SysUser doctorUser = userMapper.selectById(doctor.getUserId());
                vo.setDoctorName(doctorUser != null ? doctorUser.getRealName() : "医生");
            }
            MedicalDepartment dept = departmentMapper.selectById(appointment.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }

            LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
            queueWrapper.eq(MedicalQueue::getAppointmentId, appointment.getId());
            MedicalQueue queue = queueMapper.selectOne(queueWrapper);
            if (queue != null) {
                vo.setQueueNo(queue.getQueueNo());
            }

            boolean pending = pendingAppointmentIds.contains(appointment.getId());
            vo.setPendingCancelApproval(pending);
            vo.setCanDirectCancel("待确认".equals(appointment.getStatus()) && !pending);
            vo.setCanApplyCancel("已确认".equals(appointment.getStatus()) && !pending);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitCancelRegApproval(Long appointmentId, Long userId, String reason) {
        MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该预约");
        }
        if (!"已确认".equals(appointment.getStatus())) {
            throw new RuntimeException("仅已确认状态的预约需要提交退号申请");
        }
        if (hasPendingCancelApproval(appointmentId, userId)) {
            throw new RuntimeException("该预约已有待审核的退号申请");
        }

        SysUser student = userMapper.selectById(userId);
        MedicalDoctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        String doctorName = "医生";
        if (doctor != null) {
            SysUser doctorUser = userMapper.selectById(doctor.getUserId());
            if (doctorUser != null) {
                doctorName = doctorUser.getRealName();
            }
        }
        MedicalDepartment dept = departmentMapper.selectById(appointment.getDeptId());

        String approvalNo = generateApprovalNo();
        MedicalApproval approval = new MedicalApproval();
        approval.setApprovalNo(approvalNo);
        approval.setApprovalType("退号");
        approval.setBusinessId(appointmentId);
        approval.setAppointmentId(appointmentId);
        approval.setApplicantId(userId);
        approval.setReason(reason);
        approval.setStatus("待审核");
        approval.setPatientName(student != null ? student.getRealName() : "");
        approval.setPatientNo(student != null ? student.getUsername() : "");
        approval.setAppointmentNo(appointment.getAppointmentNo());
        approval.setDoctorName(doctorName);
        approval.setDeptName(dept != null ? dept.getDeptName() : "");
        approval.setTimeSlot(appointment.getTimeSlot());
        approval.setVisitDate(appointment.getAppointmentDate() != null
                ? appointment.getAppointmentDate().toString() : "");
        approval.setRefundAmount(DEFAULT_REGISTER_FEE);
        approval.setContactPhone(student != null ? student.getPhone() : "");
        medicalApprovalMapper.insert(approval);

        appointment.setStatus("待审批");
        appointmentMapper.updateById(appointment);

        log.info("退号申请提交成功：appointmentId={}, approvalNo={}", appointmentId, approvalNo);
        return approvalNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void directCancelAppointment(Long appointmentId, Long userId) {
        appointmentBusinessService.cancelAppointment(appointmentId, userId);
    }

    @Override
    public PageResultVO<CancelRegVO> queryCancelApplyList(SickApplyQueryDTO queryDTO) {
        Page<MedicalApproval> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MedicalApproval::getApprovalType, CANCEL_REG_TYPES);

        String statusFilter = mapAuditStatusToString(queryDTO.getAuditStatus());
        if (statusFilter != null) {
            wrapper.eq(MedicalApproval::getStatus, statusFilter);
        }
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            wrapper.ge(MedicalApproval::getCreateTime, queryDTO.getStartTime() + " 00:00:00");
        }
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            wrapper.le(MedicalApproval::getCreateTime, queryDTO.getEndTime() + " 23:59:59");
        }
        wrapper.orderByDesc(MedicalApproval::getCreateTime);

        IPage<MedicalApproval> approvalPage = medicalApprovalMapper.selectPage(page, wrapper);

        List<CancelRegVO> voList = approvalPage.getRecords().stream()
                .map(this::buildCancelRegVO)
                .collect(Collectors.toList());

        return new PageResultVO<>(voList, approvalPage.getTotal(), approvalPage.getPages(),
                approvalPage.getCurrent(), approvalPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doCancelAudit(Long cancelId, String auditStatus, String remark, Long auditorId) {
        MedicalApproval approval = medicalApprovalMapper.selectById(cancelId);
        if (approval == null) {
            throw new RuntimeException("退号申请不存在");
        }
        if (!CANCEL_REG_TYPES.contains(approval.getApprovalType())) {
            throw new RuntimeException("审批类型不正确");
        }
        if (!"待审核".equals(approval.getStatus())) {
            throw new RuntimeException("该申请已处理，请勿重复操作");
        }

        approval.setAuditorId(auditorId);
        approval.setAuditContent(remark);
        approval.setAuditTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());
        approval.setStatus(auditStatus);

        if ("已通过".equals(auditStatus)) {
            Long appointmentId = approval.getAppointmentId() != null
                    ? approval.getAppointmentId() : approval.getBusinessId();
            if (appointmentId == null) {
                throw new RuntimeException("关联预约不存在");
            }
            MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
            if (appointment == null) {
                throw new RuntimeException("关联预约不存在");
            }
            Long applicantId = approval.getApplicantId();
            if ("待确认".equals(appointment.getStatus()) || "已确认".equals(appointment.getStatus())
                    || "待审批".equals(appointment.getStatus())) {
                appointmentBusinessService.cancelAppointment(appointmentId, applicantId);
            }
            log.info("退号审批通过：cancelId={}, appointmentId={}", cancelId, appointmentId);
        } else if ("已驳回".equals(auditStatus)) {
            Long appointmentId = approval.getAppointmentId() != null
                    ? approval.getAppointmentId() : approval.getBusinessId();
            if (appointmentId != null) {
                MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
                if (appointment != null && "待审批".equals(appointment.getStatus())) {
                    appointment.setStatus("已确认");
                    appointmentMapper.updateById(appointment);
                }
            }
            log.info("退号审批驳回：cancelId={}", cancelId);
        } else {
            throw new RuntimeException("审核状态不正确");
        }

        return medicalApprovalMapper.updateById(approval) > 0;
    }

    private CancelRegVO buildCancelRegVO(MedicalApproval approval) {
        CancelRegVO vo = new CancelRegVO();
        vo.setId(approval.getId());
        vo.setReason(approval.getReason());
        vo.setStatus(convertStatusToInt(approval.getStatus()));
        vo.setStatusName(getStatusName(approval.getStatus()));
        vo.setCreateTime(approval.getCreateTime());
        vo.setAppointmentNo(approval.getAppointmentNo());
        vo.setDoctorName(approval.getDoctorName());
        vo.setStudentNo(approval.getPatientNo());

        if (approval.getPatientName() != null && !approval.getPatientName().isEmpty()) {
            vo.setStudentName(approval.getPatientName());
        } else if (approval.getApplicantId() != null) {
            SysUser student = userMapper.selectById(approval.getApplicantId());
            vo.setStudentName(student != null ? student.getRealName() : "未知");
            if (vo.getStudentNo() == null && student != null) {
                vo.setStudentNo(student.getUsername());
            }
        } else {
            vo.setStudentName("未知");
        }

        if (approval.getAppointmentNo() == null) {
            Long appointmentId = approval.getAppointmentId() != null
                    ? approval.getAppointmentId() : approval.getBusinessId();
            if (appointmentId != null) {
                MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
                if (appointment != null) {
                    vo.setAppointmentNo(appointment.getAppointmentNo());
                }
            }
        }

        vo.setAmount(approval.getRefundAmount() != null
                ? approval.getRefundAmount() : DEFAULT_REGISTER_FEE);
        return vo;
    }

    private Set<Long> getPendingCancelAppointmentIds(Long userId) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MedicalApproval::getApprovalType, CANCEL_REG_TYPES);
        wrapper.eq(MedicalApproval::getApplicantId, userId);
        wrapper.eq(MedicalApproval::getStatus, "待审核");
        return medicalApprovalMapper.selectList(wrapper).stream()
                .map(a -> a.getAppointmentId() != null ? a.getAppointmentId() : a.getBusinessId())
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private boolean hasPendingCancelApproval(Long appointmentId, Long userId) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MedicalApproval::getApprovalType, CANCEL_REG_TYPES);
        wrapper.eq(MedicalApproval::getApplicantId, userId);
        wrapper.eq(MedicalApproval::getStatus, "待审核");
        wrapper.and(w -> w.eq(MedicalApproval::getAppointmentId, appointmentId)
                .or().eq(MedicalApproval::getBusinessId, appointmentId));
        return medicalApprovalMapper.selectCount(wrapper) > 0;
    }

    private String generateApprovalNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "CR" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    private String mapAuditStatusToString(Integer auditStatus) {
        if (auditStatus == null) {
            return null;
        }
        switch (auditStatus) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已驳回";
            default: return null;
        }
    }

    private String getStatusName(String status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case "待审核": return "待审核";
            case "已通过": return "已通过";
            case "已驳回": return "已驳回";
            case "已撤回": return "已撤回";
            default: return status;
        }
    }

    private Integer convertStatusToInt(String status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case "待审核": return 0;
            case "已通过": return 1;
            case "已驳回": return 2;
            case "已撤回": return 3;
            default: return null;
        }
    }
}
