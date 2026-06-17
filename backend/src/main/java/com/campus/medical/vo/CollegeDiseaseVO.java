package com.campus.medical.vo;

import lombok.Data;

/**
 * 学院疾病分布
 */
@Data
public class CollegeDiseaseVO {
    /**
     * 学院名称
     */
    private String college;
    
    /**
     * 发病次数
     */
    private Long count;
}
