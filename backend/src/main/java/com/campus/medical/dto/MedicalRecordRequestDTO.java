package com.campus.medical.dto;

import lombok.Data;

/**
 * 诊疗记录请求DTO
 */
@Data
public class MedicalRecordRequestDTO {

    /**
     * 病历ID（更新时传入）
     */
    private Long id;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 患者用户ID
     */
    private Long userId;

    /**
     * 接诊医生ID
     */
    private Long doctorId;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 主诉
     */
    private String chiefComplaint;

    /**
     * 现病史
     */
    private String presentIllness;

    /**
     * 既往史
     */
    private String pastHistory;

    /**
     * 体格检查
     */
    private String physicalExamination;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 病种编码（非必填）
     */
    private String diseaseCode;

    /**
     * 病种名称（非必填）
     */
    private String diseaseName;

    /**
     * 医嘱
     */
    private String advice;
}
