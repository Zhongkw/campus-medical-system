package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 收入占比
 */
@Data
public class IncomeRatioVO {
    /**
     * 类型名称
     */
    private String typeName;
    
    /**
     * 占比
     */
    private BigDecimal ratio;
}
