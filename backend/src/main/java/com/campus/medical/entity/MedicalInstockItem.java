package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 药品入库明细表实体类
 */
@Data
@TableName("medical_instock_item")
public class MedicalInstockItem {

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入库ID
     */
    @TableField("instock_id")
    private Long instockId;

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
     * 批号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 有效期
     */
    @TableField("expire_date")
    private LocalDate expireDate;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}