package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 电子病假申请表实体类
 */
@Data
@TableName("medical_sick_leave")
public class MedicalSickLeave {

    /**
     * 病假申请ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 病假申请编号
     */
    @TableField("leave_no")
    private String leaveNo;

    /**
     * 学生用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 开具医生ID
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 关联病假条ID
     */
    @TableField("sick_note_id")
    private Long sickNoteId;

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
     * 诊断证明URL
     */
    @TableField("diagnosis_proof_url")
    private String diagnosisProofUrl;

    /**
     * 状态(待审核/已通过/已驳回/已撤回)
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    private Long auditorId;

    /**
     * 审核意见
     */
    @TableField("audit_content")
    private String auditContent;
    /**
     * 患者姓名（非数据库字段，由用户表关联填充）
     */
    @TableField(exist = false)
    private String patientName;

    /**
     * 患者学号（非数据库字段，由用户表关联填充）
     */
    @TableField(exist = false)
    private String patientNo;

    /**
     * 学院（非数据库字段，由用户表关联填充）
     */
    @TableField(exist = false)
    private String college;

    /**
     * 班级（非数据库字段，由用户表关联填充）
     */
    @TableField(exist = false)
    private String className;

    /**
     * 联系电话（非数据库字段，由用户表关联填充）
     */
    @TableField(exist = false)
    private String contactPhone;

    /**
     * 请假天数别名（非数据库字段，与 leaveDays 一致）
     */
    @TableField(exist = false)
    private Integer days;

    /**
     * 申请原因（非数据库字段）
     */
    @TableField(exist = false)
    private String reason;

    /**
     * 审核备注别名（非数据库字段，与 auditContent 一致）
     */
    @TableField(exist = false)
    private String auditRemark;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

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