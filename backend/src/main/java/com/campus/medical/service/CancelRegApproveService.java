package com.campus.medical.service;

import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.vo.*;

import java.util.List;

/**
 * 退号申请审批服务接口
 */
public interface CancelRegApproveService {

    /**
     * 查询学生预约列表
     */
    List<StudentAppointmentVO> getStudentAppointments(Long userId);

    /**
     * 学生提交退号申请
     */
    String submitCancelRegApproval(Long appointmentId, Long userId, String reason);

    /**
     * 学生直接退号（待确认状态）
     */
    void directCancelAppointment(Long appointmentId, Long userId);

    /**
     * 查询退号申请列表
     */
    PageResultVO<CancelRegVO> queryCancelApplyList(SickApplyQueryDTO queryDTO);

    /**
     * 审核退号申请
     */
    Boolean doCancelAudit(Long cancelId, String auditStatus, String remark, Long auditorId);
}
