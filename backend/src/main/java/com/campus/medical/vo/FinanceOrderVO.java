package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务订单列表VO
 */
@Data
public class FinanceOrderVO {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学号/登录账号
     */
    private String studentKey;

    /**
     * 收费项目摘要
     */
    private String itemName;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式（1-支付宝 2-微信 3-校园卡）
     */
    private Integer payType;
    
    /**
     * 支付方式名称
     */
    private String payTypeName;
    
    /**
     * 订单状态（0-待支付 1-已支付 2-已取消 3-已退款）
     */
    private Integer status;
    
    /**
     * 订单状态名称
     */
    private String statusName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 缴费时间
     */
    private LocalDateTime payTime;
}
