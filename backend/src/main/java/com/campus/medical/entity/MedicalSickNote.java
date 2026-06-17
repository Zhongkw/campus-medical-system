package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 电子病假条表实体类
 */
@Data
@TableName("medical_sick_note")
public class MedicalSickNote {

    /**
     * 病假条ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 病假条编号
     */
    @TableField("sick_note_no")
    private String sickNoteNo;

    /**
     * 关联病历ID
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 患者用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 开具医生ID
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 诊断结果
     */
    @TableField("diagnosis")
    private String diagnosis;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 请假天数
     */
    @TableField("leave_days")
    private Integer leaveDays;

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