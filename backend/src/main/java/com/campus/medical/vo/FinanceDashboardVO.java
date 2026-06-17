package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计VO
 */
@Data
public class FinanceDashboardVO {

    /**
     * 今日营收
     */
    private BigDecimal todayIncome;

    /**
     * 本月营收
     */
    private BigDecimal monthIncome;

    /**
     * 待缴费订单数
     */
    private Integer unpaidOrderCount;

    /**
     * 今日退费金额
     */
    private BigDecimal todayRefund;

    /**
     * 本月退费金额
     */
    private BigDecimal monthRefund;
}
