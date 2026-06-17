package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 日营收数据
 */
@Data
public class DailyIncomeVO {
    /**
     * 日期
     */
    private String date;
    
    /**
     * 营收金额
     */
    private BigDecimal income;
}
