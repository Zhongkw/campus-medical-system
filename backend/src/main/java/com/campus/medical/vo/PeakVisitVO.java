package com.campus.medical.vo;

import lombok.Data;

/**
 * 高峰时段
 */
@Data
public class PeakVisitVO {
    /**
     * 时段
     */
    private String timeSlot;
    
    /**
     * 就诊人数
     */
    private Long visitCount;
}
