package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 财务对账汇总VO
 */
@Data
public class ReconciliationVO {

    private BigDecimal totalIncome = BigDecimal.ZERO;

    private BigDecimal wechatIncome = BigDecimal.ZERO;

    private BigDecimal alipayIncome = BigDecimal.ZERO;

    private BigDecimal campusIncome = BigDecimal.ZERO;

    private BigDecimal totalRefund = BigDecimal.ZERO;

    private List<ReconciliationDetailVO> details = new ArrayList<>();
}
