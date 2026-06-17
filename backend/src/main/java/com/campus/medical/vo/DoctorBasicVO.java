package com.campus.medical.vo;

import lombok.Data;

@Data
public class DoctorBasicVO {
    private Long id;
    private Long userId;
    private String doctorName;
    private String title;
    private String specialty;
    private String phone;
    private String status;
}
