package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 候诊队列表实体类
 */
@Data
@TableName("medical_queue")
public class MedicalQueue {

    /**
     * 队列ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 排队号
     */
    @TableField("queue_no")
    private String queueNo;

    /**
     * 关联预约ID
     */
    @TableField("appointment_id")
    private Long appointmentId;

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
     * 科室ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 排队日期
     */
    @TableField("queue_date")
    private LocalDate queueDate;

    /**
     * 主要症状
     */
    @TableField("symptom")
    private String symptom;

    /**
     * 状态(候诊中/就诊中/已完成/已过号)
     */
    @TableField("status")
    private String status;

    /**
     * 等待时长(分钟)
     */
    @TableField("wait_time")
    private Integer waitTime;

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