package com.campus.medical.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 病假申请请求DTO
 */
@Data
public class SickLeaveRequestDTO {

    /**
     * 就诊日期
     */
    private LocalDate visitDate;

    /**
     * 就诊科室
     */
    private String department;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 诊断证明URL
     */
    private String diagnosisProofUrl;
}
