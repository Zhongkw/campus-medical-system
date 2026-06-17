package com.campus.medical.vo;

import lombok.Data;

/**
 * 异常疾病
 */
@Data
public class AbnormalDiseaseVO {
    /**
     * 疾病名称
     */
    private String diseaseName;
    
    /**
     * 今日发病数
     */
    private Long todayCount;
    
    /**
     * 预警阈值
     */
    private Integer threshold;
    
    /**
     * 是否异常
     */
    private Boolean isAbnormal;
}
