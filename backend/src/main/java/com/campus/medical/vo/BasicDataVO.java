package com.campus.medical.vo;

import lombok.Data;

@Data
public class BasicDataVO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private String status;
}
