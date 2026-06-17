package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细表实体类
 */
@Data
@TableName("medical_order_item")
public class MedicalOrderItem {

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 收费项目名称
     */
    @TableField("item_name")
    private String itemName;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 科室ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 总价（与amount保持一致）
     */
    @TableField("total_price")
    private java.math.BigDecimal totalPrice;
}