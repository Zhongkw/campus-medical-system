package com.campus.medical.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicineManageDTO {
    private Long id;
    private String categoryCode;
    private String medicineName;
    private String spec;
    private String description;
    private String unit;
    private BigDecimal price;
}
