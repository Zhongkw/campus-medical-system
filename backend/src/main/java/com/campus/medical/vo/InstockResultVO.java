package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 入库结果VO
 */
@Data
public class InstockResultVO {

    /**
     * 入库单ID
     */
    private Long instockId;

    /**
     * 入库单号
     */
    private String instockNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 入库明细列表
     */
    private List<InstockItemVO> items;

    /**
     * 提示信息
     */
    private String message;

    @Data
    public static class InstockItemVO {
        /**
         * 明细ID
         */
        private Long id;

        /**
         * 药品名称
         */
        private String medicineName;

        /**
         * 规格
         */
        private String spec;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 小计
         */
        private BigDecimal subtotal;
    }
}
