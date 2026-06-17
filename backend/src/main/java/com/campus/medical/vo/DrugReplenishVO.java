package com.campus.medical.vo;

import lombok.Data;

/**
 * 补货需求
 */
@Data
public class DrugReplenishVO {
    /**
     * 药品名称
     */
    private String medicineName;
    
    /**
     * 当前库存
     */
    private Long currentStock;
    
    /**
     * 日消耗量
     */
    private Long dailyConsume;
    
    /**
     * 建议补货量
     */
    private Long replenishQty;
}
