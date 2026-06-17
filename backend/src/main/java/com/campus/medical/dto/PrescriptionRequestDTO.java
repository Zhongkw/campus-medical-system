package com.campus.medical.dto;

import lombok.Data;

import java.util.List;

/**
 * 处方请求DTO
 */
@Data
public class PrescriptionRequestDTO {

    /**
     * 病历ID
     */
    private Long recordId;

    /**
     * 患者用户ID
     */
    private Long userId;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 处方明细列表
     */
    private List<PrescriptionItemDTO> items;

    /**
     * 医嘱
     */
    private String advice;

    @Data
    public static class PrescriptionItemDTO {
        /**
         * 药品ID
         */
        private Long medicineId;

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
        private java.math.BigDecimal price;
    }
}
