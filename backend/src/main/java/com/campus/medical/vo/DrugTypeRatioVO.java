package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 药品类型占比
 */
@Data
public class DrugTypeRatioVO {
    /**
     * 类型名称
     */
    private String typeName;
    
    /**
     * 占比
     */
    private BigDecimal ratio;
}
