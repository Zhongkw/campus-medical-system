package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicineBasicVO {
    private Long id;
    private String categoryCode;
    private String medicineName;
    private String spec;
    private String description;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String status;
}
