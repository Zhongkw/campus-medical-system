package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.AppointmentRequestDTO;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.service.AppointmentBusinessService;
import com.campus.medical.service.CancelRegApproveService;
import com.campus.medical.service.SickLeaveBusinessService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.AppointmentResultVO;
import com.campus.medical.vo.StudentAppointmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private AppointmentBusinessService appointmentBusinessService;

    @Autowired
    private CancelRegApproveService cancelRegApproveService;

    @Autowired
    private SickLeaveBusinessService sickLeaveBusinessService;

    /**
     * 提交预约挂号（联动创建候诊队列）
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> submitRegister(@RequestBody Map<String, Object> requestBody) {
        Long scheduleId = Long.valueOf(String.valueOf(requestBody.get("scheduleId")));
        String visitDate = (String) requestBody.get("visitDate");
        String visitTime = (String) requestBody.get("visitTime");
        String symptom = requestBody.get("symptom") != null ? String.valueOf(requestBody.get("symptom")) : "待诊";

        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long userId = Long.parseLong(String.valueOf(currentUser.get("userId")));

        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setScheduleId(scheduleId);
        requestDTO.setUserId(userId);
        requestDTO.setAppointmentDate(visitDate != null ? LocalDate.parse(visitDate) : LocalDate.now());
        requestDTO.setTimeSlot(visitTime);
        requestDTO.setAppointmentType("普通");
        requestDTO.setSymptom(symptom);

        log.info("学生预约挂号: userId={}, scheduleId={}", userId, scheduleId);
        AppointmentResultVO result = appointmentBusinessService.createAppointment(requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("registerNo", result.getAppointmentNo());
        response.put("appointmentId", result.getAppointmentId());
        response.put("queueNo", result.getQueueNo());
        response.put("doctorName", result.getDoctorName());
        response.put("deptName", result.getDeptName());
        response.put("visitDate", result.getAppointmentDate());
        response.put("visitTime", result.getTimeSlot());
        response.put("status", result.getStatus());

        return ResultUtils.success("预约成功", response);
    }

    /**
     * 查询我的预约列表
     */
    @GetMapping("/appointments")
    public Result<List<StudentAppointmentVO>> getMyAppointments() {
        Long userId = getCurrentUserId();
        return ResultUtils.success(cancelRegApproveService.getStudentAppointments(userId));
    }

    /**
     * 直接退号（待确认状态）
     */
    @PostMapping("/cancelAppointment")
    public Result<Void> cancelAppointment(@RequestParam Long appointmentId) {
        Long userId = getCurrentUserId();
        cancelRegApproveService.directCancelAppointment(appointmentId, userId);
        return ResultUtils.success("退号成功", null);
    }

    /**
     * 提交退号申请（已确认状态）
     */
    @PostMapping("/cancelRegister")
    public Result<String> submitCancelRegister(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        Long appointmentId = Long.valueOf(String.valueOf(body.get("appointmentId")));
        String reason = body.get("reason") != null ? String.valueOf(body.get("reason")) : "学生申请退号";
        String approvalNo = cancelRegApproveService.submitCancelRegApproval(appointmentId, userId, reason);
        return ResultUtils.success("退号申请已提交", approvalNo);
    }

    /**
     * 提交退费申请
     */
    @PostMapping("/refund")
    public Result<String> submitRefund(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        Long orderId = Long.valueOf(String.valueOf(body.get("orderId")));
        String reason = body.get("reason") != null ? String.valueOf(body.get("reason")) : "学生申请退费";
        String approvalNo = sickLeaveBusinessService.submitRefundApproval(orderId, userId, reason);
        return ResultUtils.success("退费申请已提交", approvalNo);
    }

    /**
     * 查询我的退费申请
     */
    @GetMapping("/refundApplications")
    public Result<List<MedicalApproval>> getMyRefundApplications() {
        Long userId = getCurrentUserId();
        return ResultUtils.success(sickLeaveBusinessService.getMyRefundApplications(userId));
    }

    private Long getCurrentUserId() {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        return Long.parseLong(String.valueOf(currentUser.get("userId")));
    }
}
