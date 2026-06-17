package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 疾病排行
 */
@Data
public class DiseaseRankVO {
    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 病种分类（来自基础数据）
     */
    private String category;
    
    /**
     * 发病次数
     */
    private Long count;
    
    /**
     * 占比
     */
    private BigDecimal percentage;
}
