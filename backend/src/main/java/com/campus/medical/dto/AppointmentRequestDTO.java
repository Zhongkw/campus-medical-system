package com.campus.medical.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 预约请求DTO
 */
@Data
public class AppointmentRequestDTO {

    /**
     * 排班ID
     */
    private Long scheduleId;

    /**
     * 患者用户ID
     */
    private Long userId;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 预约时段
     */
    private String timeSlot;

    /**
     * 预约类型(普通/特殊)
     */
    private String appointmentType;

    /**
     * 主要症状
     */
    private String symptom;
}
