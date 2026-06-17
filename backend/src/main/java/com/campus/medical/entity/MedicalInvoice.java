package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发票表实体类
 */
@Data
@TableName("medical_invoice")
public class MedicalInvoice {

    /**
     * 发票ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发票编号
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 关联订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 发票金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 发票PDF地址
     */
    @TableField("invoice_url")
    private String invoiceUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}