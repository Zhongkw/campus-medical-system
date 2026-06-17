package com.campus.medical.service;

import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 诊疗记录服务接口
 */
public interface MedicalRecordService extends IBaseService<MedicalRecord> {

    /**
     * 根据患者ID查询诊疗记录列表
     */
    List<MedicalRecord> getByPatientId(Long patientId);

    /**
     * 根据医生ID查询诊疗记录列表
     */
    List<MedicalRecord> getByDoctorId(Long doctorId);

    /**
     * 根据病历号查询诊疗记录
     */
    MedicalRecord getByRecordNo(String recordNo);
}
