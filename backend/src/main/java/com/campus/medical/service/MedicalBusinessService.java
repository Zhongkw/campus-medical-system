package com.campus.medical.service;

import com.campus.medical.dto.MedicalRecordRequestDTO;
import com.campus.medical.dto.PrescriptionRequestDTO;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.vo.PatientRecordVO;
import com.campus.medical.vo.PrescriptionDetailVO;
import com.campus.medical.vo.PrescriptionResultVO;

import java.util.List;

/**
 * 诊疗业务服务接口
 */
public interface MedicalBusinessService {

    /**
     * 医生接诊（创建诊疗记录）
     * @param requestDTO 诊疗记录请求
     * @return 诊疗记录
     */
    MedicalRecord receivePatient(MedicalRecordRequestDTO requestDTO);

    /**
     * 更新诊疗记录
     * @param requestDTO 诊疗记录请求
     * @return 诊疗记录
     */
    MedicalRecord updateRecord(MedicalRecordRequestDTO requestDTO);

    /**
     * 开具处方（核心业务方法）
     * @param requestDTO 处方请求
     * @return 处方结果
     */
    PrescriptionResultVO createPrescription(PrescriptionRequestDTO requestDTO);

    /**
     * 提交处方（状态变更）
     * @param prescriptionId 处方ID
     * @param doctorId 医生ID
     */
    void submitPrescription(Long prescriptionId, Long doctorId);

    /**
     * 取消处方
     * @param prescriptionId 处方ID
     * @param doctorId 医生ID
     */
    void cancelPrescription(Long prescriptionId, Long doctorId);

    /**
     * 根据预约ID查询诊疗记录
     */
    MedicalRecord getRecordByAppointmentId(Long appointmentId);

    /**
     * 查询患者诊疗记录列表
     * @param userId 患者ID
     * @return 诊疗记录列表
     */
    List<MedicalRecord> getPatientRecords(Long userId);

    /**
     * 查询患者诊疗记录详情（含医生、处方信息）
     */
    List<PatientRecordVO> getPatientRecordDetails(Long userId);

    /**
     * 查询医生诊疗记录列表
     * @param doctorId 医生ID
     * @return 诊疗记录列表
     */
    List<MedicalRecord> getDoctorRecords(Long doctorId);

    /**
     * 查询患者处方列表
     * @param userId 患者ID
     * @return 处方列表
     */
    List<MedicalPrescription> getPatientPrescriptions(Long userId);

    /**
     * 查询医生处方列表
     * @param doctorId 医生ID
     * @return 处方列表
     */
    List<MedicalPrescription> getDoctorPrescriptions(Long doctorId);

    /**
     * 查询处方详情列表（支持状态、医生、编号筛选）
     */
    List<PrescriptionDetailVO> getPrescriptionDetailList(String status, Long doctorId, String prescriptionNo);

    /**
     * 查询处方详情
     */
    PrescriptionDetailVO getPrescriptionDetail(Long prescriptionId);

    /**
     * 药师开始配药
     */
    void startDispense(Long prescriptionId);

    /**
     * 药师完成发药
     */
    void completeDispense(Long prescriptionId);

    /**
     * 仅挂号费开单（未开药品时）
     */
    void createRegistrationOrder(Long recordId, Long doctorId);
}
