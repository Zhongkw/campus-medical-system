package com.campus.medical.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 药品入库请求DTO
 */
@Data
public class InstockRequestDTO {

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库明细列表
     */
    private List<InstockItemDTO> items;

    @Data
    public static class InstockItemDTO {
        /**
         * 药品ID
         */
        private Long medicineId;

        /**
         * 药品名称
         */
        private String medicineName;

        /**
         * 药品规格
         */
        private String spec;

        /**
         * 入库数量
         */
        private Integer quantity;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 批号
         */
        private String batchNo;

        /**
         * 有效期
         */
        private LocalDate expireDate;
    }
}
