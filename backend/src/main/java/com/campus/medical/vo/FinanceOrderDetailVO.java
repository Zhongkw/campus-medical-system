package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 财务订单详情VO
 */
@Data
public class FinanceOrderDetailVO {
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
     * 学生学号
     */
    private String studentNo;
    
    /**
     * 联系方式
     */
    private String contactPhone;
    
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
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 订单项目列表
     */
    private List<OrderItemVO> items;
    
    /**
     * 发票信息
     */
    private InvoiceVO invoice;
    
    /**
     * 订单项目VO
     */
    @Data
    public static class OrderItemVO {
        /**
         * 项目名称
         */
        private String itemName;
        
        /**
         * 项目金额
         */
        private BigDecimal amount;
        
        /**
         * 数量
         */
        private Integer quantity;
        
        /**
         * 单价
         */
        private BigDecimal price;
    }
    
    /**
     * 发票VO
     */
    @Data
    public static class InvoiceVO {
        /**
         * 发票号
         */
        private String invoiceNo;
        
        /**
         * 发票URL
         */
        private String invoiceUrl;
        
        /**
         * 开票时间
         */
        private LocalDateTime createTime;
    }
}
