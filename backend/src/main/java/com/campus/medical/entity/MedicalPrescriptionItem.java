package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方明细表实体类
 */
@Data
@TableName("medical_prescription_item")
public class MedicalPrescriptionItem {

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 处方ID
     */
    @TableField("prescription_id")
    private Long prescriptionId;

    /**
     * 药品ID
     */
    @TableField("medicine_id")
    private Long medicineId;

    /**
     * 药品名称
     */
    @TableField("medicine_name")
    private String medicineName;

    /**
     * 药品规格
     */
    @TableField("spec")
    private String spec;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 用法用量
     */
    @TableField("usage_method")
    private String usageMethod;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 总价（与amount保持一致）
     */
    @TableField("total_price")
    private java.math.BigDecimal totalPrice;
}