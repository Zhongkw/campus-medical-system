package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约表实体类
 */
@Data
@TableName("medical_appointment")
public class MedicalAppointment {

    /**
     * 预约ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预约编号
     */
    @TableField("appointment_no")
    private String appointmentNo;

    /**
     * 学生用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 医生ID
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 排班ID
     */
    @TableField("schedule_id")
    private Long scheduleId;

    /**
     * 科室ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 预约日期
     */
    @TableField("appointment_date")
    private LocalDate appointmentDate;

    /**
     * 预约时段
     */
    @TableField("time_slot")
    private String timeSlot;

    /**
     * 预约类型(普通/特殊)
     */
    @TableField("appointment_type")
    private String appointmentType;

    /**
     * 状态(待确认/已确认/已完成/已取消/待审批)
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}