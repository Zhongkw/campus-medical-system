package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务对账明细VO
 */
@Data
public class ReconciliationDetailVO {

    private String payMethod;

    private Integer orderCount;

    private BigDecimal totalAmount;

    private Integer refundCount;

    private BigDecimal refundAmount;

    private BigDecimal actualAmount;

    private String reconciliationStatus;
}
