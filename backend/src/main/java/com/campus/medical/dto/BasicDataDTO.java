package com.campus.medical.dto;

import lombok.Data;

@Data
public class BasicDataDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
}
