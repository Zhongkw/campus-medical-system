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
 * 药品表实体类
 */
@Data
@TableName("medical_medicine")
public class MedicalMedicine {

    /**
     * 药品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 库存数量（与stock保持一致）
     */
    @TableField(exist = false)
    private Long stockQuantity;

    /**
     * 最低库存阈值
     */
    @TableField("min_stock")
    private Integer minStock;

    /**
     * 药品分类
     */
    @TableField("category")
    private String category;

    /**
     * 药品作用说明
     */
    @TableField("description")
    private String description;

    /**
     * 生产厂家
     */
    @TableField("manufacturer")
    private String manufacturer;

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
     * 状态(0-禁用,1-正常)
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