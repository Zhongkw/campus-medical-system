package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方表实体类
 */
@Data
@TableName("medical_prescription")
public class MedicalPrescription {

    /**
     * 处方ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 处方编号
     */
    @TableField("prescription_no")
    private String prescriptionNo;

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
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态(草稿/已提交/待配药/配药中/已完成/已取消)
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