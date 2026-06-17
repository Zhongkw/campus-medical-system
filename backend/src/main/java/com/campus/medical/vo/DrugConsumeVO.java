package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 药品消耗排行
 */
@Data
public class DrugConsumeVO {
    /**
     * 药品名称
     */
    private String medicineName;
    
    /**
     * 消耗数量
     */
    private Long consumeCount;
    
    /**
     * 消耗金额
     */
    private BigDecimal consumeAmount;
}
