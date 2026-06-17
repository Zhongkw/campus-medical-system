package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生排班表实体类
 */
@Data
@TableName("medical_schedule")
public class MedicalSchedule {

    /**
     * 排班ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 排班日期
     */
    @TableField("schedule_date")
    private LocalDate scheduleDate;

    /**
     * 出诊时段(08:00-12:00/14:00-17:30)
     */
    @TableField("time_slot")
    private String timeSlot;

    /**
     * 最大号源数
     */
    @TableField("max_num")
    private Integer maxNum;

    /**
     * 剩余号源数
     */
    @TableField("remain_num")
    private Integer remainNum;

    /**
     * 号源类型(普通/专家/急诊)
     */
    @TableField("schedule_type")
    private String scheduleType;

    /**
     * 状态(0-取消,1-正常)
     */
    @TableField("status")
    private Integer status;

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