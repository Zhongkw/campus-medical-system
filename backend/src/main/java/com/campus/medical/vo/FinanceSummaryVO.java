package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 财务汇总
 */
@Data
public class FinanceSummaryVO {
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 总退款
     */
    private BigDecimal totalRefund;
    
    /**
     * 未支付数量
     */
    private Long unpaidCount;
}
