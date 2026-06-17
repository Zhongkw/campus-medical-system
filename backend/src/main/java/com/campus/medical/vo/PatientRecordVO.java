package com.campus.medical.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生端诊疗记录详情VO
 */
@Data
public class PatientRecordVO {

    private Long id;

    private String visitDate;

    private String department;

    private String doctorName;

    private String symptom;

    private String diagnosis;

    private String status;

    private String advice;

    private Long prescriptionId;

    private String prescriptionNo;

    private String prescribeDoctorName;

    private String prescribeDate;

    private List<PrescriptionDetailVO.MedicineItemVO> medicines = new ArrayList<>();
}
