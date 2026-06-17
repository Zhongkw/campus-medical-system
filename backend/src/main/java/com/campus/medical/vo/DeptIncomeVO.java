package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 科室营收
 */
@Data
public class DeptIncomeVO {
    /**
     * 科室名称
     */
    private String deptName;
    
    /**
     * 营收金额
     */
    private BigDecimal income;
}
