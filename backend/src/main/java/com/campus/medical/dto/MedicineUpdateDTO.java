package com.campus.medical.dto;

import lombok.Data;

/**
 * 药品库存信息更新
 */
@Data
public class MedicineUpdateDTO {

    private Long medicineId;

    private Integer stock;

    private Integer minStock;

    private String expireDate;
}
