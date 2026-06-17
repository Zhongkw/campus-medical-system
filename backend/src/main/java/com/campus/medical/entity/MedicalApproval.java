package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批表实体类
 */
@Data
@TableName("medical_approval")
public class MedicalApproval {

    /**
     * 审批ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审批编号
     */
    @TableField("approval_no")
    private String approvalNo;

    /**
     * 审批类型(特殊挂号/退号/退费)
     */
    @TableField("approval_type")
    private String approvalType;

    /**
     * 关联业务ID
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 申请人ID
     */
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 申请原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态(待审核/已通过/已驳回)
     */
    @TableField("status")
    private String status;

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
     * 患者姓名（冗余字段）
     */
    @TableField("patient_name")
    private String patientName;

    /**
     * 预约编号（冗余字段）
     */
    @TableField("appointment_no")
    private String appointmentNo;

    /**
     * 退费金额
     */
    @TableField("refund_amount")
    private java.math.BigDecimal refundAmount;
    /**
     * 患者学号（冗余字段）
     */
    @TableField("patient_no")
    private String patientNo;

    /**
     * 医生姓名（冗余字段）
     */
    @TableField("doctor_name")
    private String doctorName;

    /**
     * 时段（冗余字段）
     */
    @TableField("time_slot")
    private String timeSlot;

    /**
     * 联系电话（冗余字段）
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 科室名称（冗余字段）
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 就诊日期（冗余字段）
     */
    @TableField("visit_date")
    private String visitDate;

    /**
     * 预约ID（关联业务）
     */
    @TableField("appointment_id")
    private Long appointmentId;

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