package com.campus.medical.vo;

import lombok.Data;

/**
 * 就诊趋势
 */
@Data
public class VisitTrendVO {
    /**
     * 日期
     */
    private String date;
    
    /**
     * 就诊人数
     */
    private Long count;
}
