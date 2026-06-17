package com.campus.medical.service;

import com.campus.medical.dto.SickLeaveAuditRequestDTO;
import com.campus.medical.dto.SickLeaveRequestDTO;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.entity.MedicalSickNote;
import com.campus.medical.vo.CollegeSickStatVO;
import com.campus.medical.vo.SickLeaveDetailVO;
import com.campus.medical.vo.SickLeaveSummaryVO;

import java.util.List;

/**
 * 病假申请审批业务服务接口
 */
public interface SickLeaveBusinessService {

    // ========== 学生端：病假申请 ==========

    /**
     * 提交病假申请
     * @param requestDTO 病假申请请求
     * @param userId 申请人ID
     * @return 病假申请编号
     */
    String submitSickLeave(SickLeaveRequestDTO requestDTO, Long userId);

    /**
     * 查询我的病假申请列表
     * @param userId 用户ID
     * @param status 申请状态
     * @return 病假申请列表
     */
    List<MedicalSickLeave> getMySickLeaves(Long userId, String status);

    /**
     * 查询病假申请详情
     * @param applyId 申请ID
     * @param userId 用户ID
     * @return 病假申请详情
     */
    SickLeaveDetailVO getSickLeaveDetail(Long applyId, Long userId);

    /**
     * 撤回病假申请（仅待审核状态可撤回）
     * @param applyId 申请ID
     * @param userId 用户ID
     * @return 撤回结果
     */
    Boolean withdrawSickLeave(Long applyId, Long userId);

    // ========== 医生端：开具病假条 ==========

    /**
     * 医生开具病假条
     * @param recordId 病历ID
     * @param userId 患者ID
     * @param doctorId 医生ID
     * @param diagnosis 诊断结果
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 病假条编号
     */
    String createSickNote(Long recordId, Long userId, Long doctorId, 
                         String diagnosis, java.time.LocalDate startDate, 
                         java.time.LocalDate endDate);

    /**
     * 查询医生开具的病假条列表
     * @param doctorId 医生ID
     * @return 病假条列表
     */
    List<MedicalSickNote> getDoctorSickNotes(Long doctorId);

    // ========== 审批端：病假申请审批 ==========

    /**
     * 查询病假申请列表（审批端）
     * @param college 学院名称
     * @param status 审批状态
     * @return 病假申请列表
     */
    List<SickLeaveDetailVO> getSickLeaveListForApproval(String college, String status);

    /**
     * 审批病假申请
     * @param requestDTO 审批请求
     * @return 审批结果
     */
    Boolean auditSickLeave(SickLeaveAuditRequestDTO requestDTO);

    /**
     * 批量审批病假申请
     * @param applyIds 申请ID列表
     * @param auditStatus 审批状态
     * @param auditContent 审批意见
     * @param auditorId 审核人ID
     * @return 审批结果
     */
    Boolean batchAuditSickLeave(List<Long> applyIds, String auditStatus, 
                                String auditContent, Long auditorId);

    /**
     * 统计各学院病假申请数量
     * @return 学院统计列表
     */
    List<CollegeSickStatVO> getCollegeSickStat();

    /**
     * 病假申请汇总统计
     */
    SickLeaveSummaryVO getSickLeaveSummary();

    // ========== 特殊挂号/退费审批 ==========

    /**
     * 提交特殊挂号申请
     * @param scheduleId 排班ID
     * @param applicantId 申请人ID
     * @param reason 申请原因
     * @return 审批编号
     */
    String submitSpecialAppointmentApproval(Long scheduleId, Long applicantId, String reason);

    /**
     * 提交退费申请
     * @param orderId 订单ID
     * @param applicantId 申请人ID
     * @param reason 申请原因
     * @return 审批编号
     */
    String submitRefundApproval(Long orderId, Long applicantId, String reason);

    /**
     * 审批业务申请（特殊挂号/退费）
     * @param approvalNo 审批编号
     * @param auditStatus 审批状态
     * @param auditContent 审批意见
     * @param auditorId 审核人ID
     * @return 审批结果
     */
    Boolean auditApproval(String approvalNo, String auditStatus, 
                         String auditContent, Long auditorId);

    /**
     * 查询待审批列表
     * @param approvalType 审批类型
     * @param status 审批状态
     * @return 审批列表
     */
    List<MedicalApproval> getApprovalList(String approvalType, String status);

    /**
     * 查询我的退费申请
     */
    List<MedicalApproval> getMyRefundApplications(Long userId);
}
