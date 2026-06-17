package com.campus.medical.service;

import com.campus.medical.dto.AppointmentRequestDTO;
import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.vo.AppointmentResultVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约挂号业务服务接口
 */
public interface AppointmentBusinessService {

    /**
     * 查询医生排班列表
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<MedicalSchedule> getDoctorSchedules(Long doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * 查询科室排班列表
     * @param deptId 科室ID
     * @param date 日期
     * @return 排班列表
     */
    List<MedicalSchedule> getDeptSchedules(Long deptId, LocalDate date);

    /**
     * 创建预约（核心业务方法）
     * @param requestDTO 预约请求
     * @return 预约结果
     */
    AppointmentResultVO createAppointment(AppointmentRequestDTO requestDTO);

    /**
     * 取消预约
     * @param appointmentId 预约ID
     * @param userId 用户ID
     */
    void cancelAppointment(Long appointmentId, Long userId);

    /**
     * 确认预约（医生操作）
     * @param appointmentId 预约ID
     * @param doctorId 医生ID
     */
    void confirmAppointment(Long appointmentId, Long doctorId);

    /**
     * 完成预约（就诊完成）
     * @param appointmentId 预约ID
     * @param doctorId 医生ID
     */
    void completeAppointment(Long appointmentId, Long doctorId);

    /**
     * 查询用户预约列表
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 预约列表
     */
    List<MedicalAppointment> getUserAppointments(Long userId, String status);

    /**
     * 查询医生预约列表
     * @param doctorId 医生ID
     * @param date 日期
     * @return 预约列表
     */
    List<MedicalAppointment> getDoctorAppointments(Long doctorId, LocalDate date);
}
