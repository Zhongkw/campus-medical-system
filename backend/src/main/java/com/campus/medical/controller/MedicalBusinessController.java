package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.MedicalRecordRequestDTO;
import com.campus.medical.dto.PrescriptionRequestDTO;
import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.service.MedicalBusinessService;
import com.campus.medical.vo.PrescriptionDetailVO;
import com.campus.medical.vo.PrescriptionResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊疗业务控制器
 */
@RestController
@RequestMapping("/api/medical/business")
public class MedicalBusinessController {

    @Autowired
    private MedicalBusinessService medicalBusinessService;

    /**
     * 医生接诊（创建诊疗记录）
     */
    @PostMapping("/receive")
    public Result<MedicalRecord> receivePatient(@RequestBody MedicalRecordRequestDTO requestDTO) {
        MedicalRecord record = medicalBusinessService.receivePatient(requestDTO);
        return ResultUtils.success("接诊成功", record);
    }

    /**
     * 更新诊疗记录
     */
    @PutMapping("/record")
    public Result<MedicalRecord> updateRecord(@RequestBody MedicalRecordRequestDTO requestDTO) {
        MedicalRecord record = medicalBusinessService.updateRecord(requestDTO);
        return ResultUtils.success("更新成功", record);
    }

    /**
     * 开具处方（核心接口）
     */
    @PostMapping("/prescription/create")
    public Result<PrescriptionResultVO> createPrescription(@RequestBody PrescriptionRequestDTO requestDTO) {
        PrescriptionResultVO result = medicalBusinessService.createPrescription(requestDTO);
        return ResultUtils.success("处方创建成功", result);
    }

    /**
     * 提交处方（扣减库存）
     */
    @PostMapping("/prescription/submit/{prescriptionId}")
    public Result<Void> submitPrescription(
            @PathVariable Long prescriptionId,
            @RequestParam Long doctorId) {
        medicalBusinessService.submitPrescription(prescriptionId, doctorId);
        return ResultUtils.success("处方已提交", null);
    }

    @PostMapping("/order/registration")
    public Result<Void> createRegistrationOrder(
            @RequestParam Long recordId,
            @RequestParam Long doctorId) {
        medicalBusinessService.createRegistrationOrder(recordId, doctorId);
        return ResultUtils.success("挂号费订单已生成", null);
    }

    /**
     * 取消处方
     */
    @PostMapping("/prescription/cancel/{prescriptionId}")
    public Result<Void> cancelPrescription(
            @PathVariable Long prescriptionId,
            @RequestParam Long doctorId) {
        medicalBusinessService.cancelPrescription(prescriptionId, doctorId);
        return ResultUtils.success("处方已取消", null);
    }

    /**
     * 根据预约ID查询诊疗记录
     */
    @GetMapping("/records/appointment/{appointmentId}")
    public Result<MedicalRecord> getRecordByAppointment(@PathVariable Long appointmentId) {
        MedicalRecord record = medicalBusinessService.getRecordByAppointmentId(appointmentId);
        return ResultUtils.success(record);
    }

    /**
     * 查询患者诊疗记录列表
     */
    @GetMapping("/records/patient/{userId}")
    public Result<List<MedicalRecord>> getPatientRecords(@PathVariable Long userId) {
        List<MedicalRecord> records = medicalBusinessService.getPatientRecords(userId);
        return ResultUtils.success(records);
    }

    /**
     * 查询患者诊疗记录详情（含医生、处方）
     */
    @GetMapping("/records/patient/{userId}/detail")
    public Result<List<com.campus.medical.vo.PatientRecordVO>> getPatientRecordDetails(@PathVariable Long userId) {
        return ResultUtils.success(medicalBusinessService.getPatientRecordDetails(userId));
    }

    /**
     * 查询医生诊疗记录列表
     */
    @GetMapping("/records/doctor/{doctorId}")
    public Result<List<MedicalRecord>> getDoctorRecords(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalBusinessService.getDoctorRecords(doctorId);
        return ResultUtils.success(records);
    }

    /**
     * 查询患者处方列表
     */
    @GetMapping("/prescriptions/patient/{userId}")
    public Result<List<MedicalPrescription>> getPatientPrescriptions(@PathVariable Long userId) {
        List<MedicalPrescription> prescriptions = medicalBusinessService.getPatientPrescriptions(userId);
        return ResultUtils.success(prescriptions);
    }

    /**
     * 查询医生处方列表
     */
    @GetMapping("/prescriptions/doctor/{doctorId}")
    public Result<List<MedicalPrescription>> getDoctorPrescriptions(@PathVariable Long doctorId) {
        List<MedicalPrescription> prescriptions = medicalBusinessService.getDoctorPrescriptions(doctorId);
        return ResultUtils.success(prescriptions);
    }

    /**
     * 查询处方详情列表（药师/医生端）
     */
    @GetMapping("/prescription/detail-list")
    public Result<List<PrescriptionDetailVO>> getPrescriptionDetailList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String prescriptionNo) {
        return ResultUtils.success(medicalBusinessService.getPrescriptionDetailList(status, doctorId, prescriptionNo));
    }

    /**
     * 查询处方详情
     */
    @GetMapping("/prescription/detail/{prescriptionId}")
    public Result<PrescriptionDetailVO> getPrescriptionDetail(@PathVariable Long prescriptionId) {
        return ResultUtils.success(medicalBusinessService.getPrescriptionDetail(prescriptionId));
    }

    /**
     * 药师开始配药
     */
    @PostMapping("/prescription/dispense/{prescriptionId}")
    public Result<Void> startDispense(@PathVariable Long prescriptionId) {
        medicalBusinessService.startDispense(prescriptionId);
        return ResultUtils.success("开始配药", null);
    }

    /**
     * 药师完成发药
     */
    @PostMapping("/prescription/complete-dispense/{prescriptionId}")
    public Result<Void> completeDispense(@PathVariable Long prescriptionId) {
        medicalBusinessService.completeDispense(prescriptionId);
        return ResultUtils.success("发药完成", null);
    }
}
