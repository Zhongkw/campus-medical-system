package com.campus.medical.vo;

import lombok.Data;

/**
 * 疾病趋势
 */
@Data
public class DiseaseTrendVO {
    /**
     * 月份
     */
    private String month;
    
    /**
     * 发病次数
     */
    private Long count;
}
