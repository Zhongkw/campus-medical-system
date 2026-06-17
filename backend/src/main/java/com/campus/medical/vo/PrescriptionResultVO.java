package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 处方结果VO
 */
@Data
public class PrescriptionResultVO {

    /**
     * 处方ID
     */
    private Long prescriptionId;

    /**
     * 处方编号
     */
    private String prescriptionNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 处方状态
     */
    private String status;

    /**
     * 处方明细列表
     */
    private List<PrescriptionItemVO> items;

    /**
     * 提示信息
     */
    private String message;

    @Data
    public static class PrescriptionItemVO {
        /**
         * 明细ID
         */
        private Long id;

        /**
         * 药品名称
         */
        private String medicineName;

        /**
         * 用法用量
         */
        private String usage;

        /**
         * 频次
         */
        private String frequency;

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
