package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.SickLeaveAuditRequestDTO;
import com.campus.medical.dto.SickLeaveRequestDTO;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.entity.MedicalSickNote;
import com.campus.medical.service.SickLeaveBusinessService;
import com.campus.medical.vo.CollegeSickStatVO;
import com.campus.medical.vo.SickLeaveDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 病假申请审批业务控制器
 */
@RestController
@RequestMapping("/api/medical/sickleave-business")
public class SickLeaveBusinessController {

    @Autowired
    private SickLeaveBusinessService sickLeaveBusinessService;

    // ========== 学生端：病假申请 ==========

    /**
     * 提交病假申请
     */
    @PostMapping("/submit")
    public Result<String> submitSickLeave(@RequestBody SickLeaveRequestDTO requestDTO,
                                          @RequestParam Long userId) {
        String leaveNo = sickLeaveBusinessService.submitSickLeave(requestDTO, userId);
        return ResultUtils.success("申请提交成功", leaveNo);
    }

    /**
     * 查询我的病假申请列表
     */
    @GetMapping("/myList")
    public Result<List<MedicalSickLeave>> getMySickLeaves(@RequestParam Long userId,
                                                           @RequestParam(required = false) String status) {
        List<MedicalSickLeave> list = sickLeaveBusinessService.getMySickLeaves(userId, status);
        return ResultUtils.success(list);
    }

    /**
     * 查询病假申请详情
     */
    @GetMapping("/detail/{applyId}")
    public Result<SickLeaveDetailVO> getSickLeaveDetail(@PathVariable Long applyId,
                                                         @RequestParam Long userId) {
        SickLeaveDetailVO detail = sickLeaveBusinessService.getSickLeaveDetail(applyId, userId);
        return ResultUtils.success(detail);
    }

    /**
     * 撤回病假申请
     */
    @PostMapping("/withdraw/{applyId}")
    public Result<Boolean> withdrawSickLeave(@PathVariable Long applyId,
                                              @RequestParam Long userId) {
        Boolean result = sickLeaveBusinessService.withdrawSickLeave(applyId, userId);
        return ResultUtils.success("撤回成功", result);
    }

    // ========== 医生端：开具病假条 ==========

    /**
     * 医生开具病假条
     */
    @PostMapping("/createSickNote")
    public Result<String> createSickNote(@RequestParam Long recordId,
                                          @RequestParam Long userId,
                                          @RequestParam Long doctorId,
                                          @RequestParam String diagnosis,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        String sickNoteNo = sickLeaveBusinessService.createSickNote(recordId, userId, doctorId, 
                                                                    diagnosis, startDate, endDate);
        return ResultUtils.success("病假条开具成功", sickNoteNo);
    }

    /**
     * 查询医生开具的病假条列表
     */
    @GetMapping("/doctorSickNotes")
    public Result<List<MedicalSickNote>> getDoctorSickNotes(@RequestParam Long doctorId) {
        List<MedicalSickNote> list = sickLeaveBusinessService.getDoctorSickNotes(doctorId);
        return ResultUtils.success(list);
    }

    // ========== 审批端：病假申请审批 ==========

    /**
     * 查询病假申请列表（审批端）
     */
    @GetMapping("/approvalList")
    public Result<List<SickLeaveDetailVO>> getSickLeaveListForApproval(
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String status) {
        List<SickLeaveDetailVO> list = sickLeaveBusinessService.getSickLeaveListForApproval(college, status);
        return ResultUtils.success(list);
    }

    /**
     * 审批病假申请
     */
    @PostMapping("/audit")
    public Result<Boolean> auditSickLeave(@RequestBody SickLeaveAuditRequestDTO requestDTO) {
        Boolean result = sickLeaveBusinessService.auditSickLeave(requestDTO);
        return ResultUtils.success("审批完成", result);
    }

    /**
     * 批量审批病假申请
     */
    @PostMapping("/batchAudit")
    public Result<Boolean> batchAuditSickLeave(@RequestBody List<Long> applyIds,
                                                @RequestParam String auditStatus,
                                                @RequestParam(required = false) String auditContent,
                                                @RequestParam Long auditorId) {
        Boolean result = sickLeaveBusinessService.batchAuditSickLeave(applyIds, auditStatus, 
                                                                      auditContent, auditorId);
        return ResultUtils.success("批量审批完成", result);
    }

    /**
     * 统计各学院病假申请数量
     */
    @GetMapping("/collegeStat")
    public Result<List<CollegeSickStatVO>> getCollegeSickStat() {
        List<CollegeSickStatVO> stat = sickLeaveBusinessService.getCollegeSickStat();
        return ResultUtils.success(stat);
    }

    /**
     * 病假申请汇总统计
     */
    @GetMapping("/summary")
    public Result<com.campus.medical.vo.SickLeaveSummaryVO> getSickLeaveSummary() {
        return ResultUtils.success(sickLeaveBusinessService.getSickLeaveSummary());
    }

    // ========== 特殊挂号/退费审批 ==========

    /**
     * 提交特殊挂号申请
     */
    @PostMapping("/submitSpecialAppointment")
    public Result<String> submitSpecialAppointmentApproval(@RequestParam Long scheduleId,
                                                            @RequestParam Long applicantId,
                                                            @RequestParam String reason) {
        String approvalNo = sickLeaveBusinessService.submitSpecialAppointmentApproval(scheduleId, applicantId, reason);
        return ResultUtils.success("申请已提交", approvalNo);
    }

    /**
     * 提交退费申请
     */
    @PostMapping("/submitRefund")
    public Result<String> submitRefundApproval(@RequestParam Long orderId,
                                                @RequestParam Long applicantId,
                                                @RequestParam String reason) {
        String approvalNo = sickLeaveBusinessService.submitRefundApproval(orderId, applicantId, reason);
        return ResultUtils.success("申请已提交", approvalNo);
    }

    /**
     * 审批业务申请（特殊挂号/退费）
     */
    @PostMapping("/auditApproval")
    public Result<Boolean> auditApproval(@RequestParam String approvalNo,
                                          @RequestParam String auditStatus,
                                          @RequestParam(required = false) String auditContent,
                                          @RequestParam Long auditorId) {
        Boolean result = sickLeaveBusinessService.auditApproval(approvalNo, auditStatus, 
                                                                auditContent, auditorId);
        return ResultUtils.success("审批完成", result);
    }

    /**
     * 查询业务审批列表（特殊挂号/退费）
     */
    @GetMapping("/businessApprovalList")
    public Result<List<MedicalApproval>> getApprovalList(
            @RequestParam(required = false) String approvalType,
            @RequestParam(required = false) String status) {
        List<MedicalApproval> list = sickLeaveBusinessService.getApprovalList(approvalType, status);
        return ResultUtils.success(list);
    }
}
