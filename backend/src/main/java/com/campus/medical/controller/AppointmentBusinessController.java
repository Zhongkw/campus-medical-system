package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.AppointmentRequestDTO;
import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.service.AppointmentBusinessService;
import com.campus.medical.vo.AppointmentResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约挂号业务控制器
 */
@RestController
@RequestMapping("/api/medical/appointment-business")
public class AppointmentBusinessController {

    @Autowired
    private AppointmentBusinessService appointmentBusinessService;

    /**
     * 查询医生排班列表
     * @param doctorId 医生ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     */
    @GetMapping("/schedules/doctor/{doctorId}")
    public Result<List<MedicalSchedule>> getDoctorSchedules(
            @PathVariable Long doctorId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<MedicalSchedule> schedules = appointmentBusinessService.getDoctorSchedules(doctorId, start, end);
        return ResultUtils.success(schedules);
    }

    /**
     * 查询科室排班列表
     * @param deptId 科室ID
     * @param date 日期 yyyy-MM-dd
     */
    @GetMapping("/schedules/dept/{deptId}")
    public Result<List<MedicalSchedule>> getDeptSchedules(
            @PathVariable Long deptId,
            @RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<MedicalSchedule> schedules = appointmentBusinessService.getDeptSchedules(deptId, queryDate);
        return ResultUtils.success(schedules);
    }

    /**
     * 创建预约（核心接口）
     * @param requestDTO 预约请求
     */
    @PostMapping("/create")
    public Result<AppointmentResultVO> createAppointment(@RequestBody AppointmentRequestDTO requestDTO) {
        AppointmentResultVO result = appointmentBusinessService.createAppointment(requestDTO);
        return ResultUtils.success("预约成功", result);
    }

    /**
     * 取消预约
     * @param appointmentId 预约ID
     * @param userId 用户ID（从JWT Token中获取）
     */
    @PostMapping("/cancel/{appointmentId}")
    public Result<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long userId) {
        appointmentBusinessService.cancelAppointment(appointmentId, userId);
        return ResultUtils.success("取消成功", null);
    }

    /**
     * 确认预约（医生操作）
     * @param appointmentId 预约ID
     * @param doctorId 医生ID（从JWT Token中获取）
     */
    @PostMapping("/confirm/{appointmentId}")
    public Result<Void> confirmAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId) {
        appointmentBusinessService.confirmAppointment(appointmentId, doctorId);
        return ResultUtils.success("确认成功", null);
    }

    /**
     * 完成预约（医生操作）
     * @param appointmentId 预约ID
     * @param doctorId 医生ID（从JWT Token中获取）
     */
    @PostMapping("/complete/{appointmentId}")
    public Result<Void> completeAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId) {
        appointmentBusinessService.completeAppointment(appointmentId, doctorId);
        return ResultUtils.success("完成成功", null);
    }

    /**
     * 查询用户预约列表
     * @param userId 用户ID
     * @param status 状态（可选）：待确认/已确认/已完成/已取消
     */
    @GetMapping("/user/{userId}")
    public Result<List<MedicalAppointment>> getUserAppointments(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        List<MedicalAppointment> appointments = appointmentBusinessService.getUserAppointments(userId, status);
        return ResultUtils.success(appointments);
    }

    /**
     * 查询医生今日预约列表
     * @param doctorId 医生ID
     * @param date 日期 yyyy-MM-dd（可选，默认今天）
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalAppointment>> getDoctorAppointments(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<MedicalAppointment> appointments = appointmentBusinessService.getDoctorAppointments(doctorId, queryDate);
        return ResultUtils.success(appointments);
    }
}
