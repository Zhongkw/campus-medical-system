package com.campus.medical.service;

import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 处方服务接口
 */
public interface MedicalPrescriptionService extends IBaseService<MedicalPrescription> {

    /**
     * 根据患者ID查询处方列表
     */
    List<MedicalPrescription> getByPatientId(Long patientId);

    /**
     * 根据医生ID查询处方列表
     */
    List<MedicalPrescription> getByDoctorId(Long doctorId);

    /**
     * 根据处方编号查询处方
     */
    MedicalPrescription getByPrescriptionNo(String prescriptionNo);
}
