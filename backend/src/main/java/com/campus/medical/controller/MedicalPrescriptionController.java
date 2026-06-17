package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalPrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处方控制器
 */
@RestController
@RequestMapping("/api/medical/prescription")
public class MedicalPrescriptionController extends BaseController<MedicalPrescription> {

    @Autowired
    private MedicalPrescriptionService medicalPrescriptionService;

    @Override
    protected IBaseService<MedicalPrescription> getService() {
        return medicalPrescriptionService;
    }

    /**
     * 根据患者ID查询处方列表
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalPrescription>> getByPatientId(@PathVariable Long patientId) {
        return ResultUtils.success(medicalPrescriptionService.getByPatientId(patientId));
    }

    /**
     * 根据医生ID查询处方列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalPrescription>> getByDoctorId(@PathVariable Long doctorId) {
        return ResultUtils.success(medicalPrescriptionService.getByDoctorId(doctorId));
    }

    /**
     * 根据处方编号查询处方
     */
    @GetMapping("/no/{prescriptionNo}")
    public Result<MedicalPrescription> getByPrescriptionNo(@PathVariable String prescriptionNo) {
        return ResultUtils.success(medicalPrescriptionService.getByPrescriptionNo(prescriptionNo));
    }
}
