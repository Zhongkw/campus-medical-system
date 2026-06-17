package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 关联处方ID
     */
    private Long prescriptionId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;

    /**
     * 关联发票信息
     */
    private InvoiceVO invoice;

    @Data
    public static class OrderItemVO {
        /**
         * 明细ID
         */
        private Long id;

        /**
         * 项目名称
         */
        private String itemName;

        /**
         * 项目类型(挂号费/诊疗费/药品费/检查费)
         */
        private String itemType;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 金额
         */
        private BigDecimal amount;
    }

    @Data
    public static class InvoiceVO {
        /**
         * 发票ID
         */
        private Long invoiceId;

        /**
         * 发票编号
         */
        private String invoiceNo;

        /**
         * 发票金额
         */
        private BigDecimal totalAmount;

        /**
         * 发票PDF地址
         */
        private String invoiceUrl;

        /**
         * 创建时间
         */
        private String createTime;
    }
}
