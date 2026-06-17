package com.campus.medical.vo;

import lombok.Data;

/**
 * 预约结果VO
 */
@Data
public class AppointmentResultVO {

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 预约编号
     */
    private String appointmentNo;

    /**
     * 排队号
     */
    private String queueNo;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 预约日期
     */
    private String appointmentDate;

    /**
     * 预约时段
     */
    private String timeSlot;

    /**
     * 预约状态
     */
    private String status;

    /**
     * 提示信息
     */
    private String message;
}
