package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊疗记录控制器
 */
@RestController
@RequestMapping("/api/medical/record")
public class MedicalRecordController extends BaseController<MedicalRecord> {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Override
    protected IBaseService<MedicalRecord> getService() {
        return medicalRecordService;
    }

    /**
     * 根据患者ID查询诊疗记录列表
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalRecord>> getByPatientId(@PathVariable Long patientId) {
        return ResultUtils.success(medicalRecordService.getByPatientId(patientId));
    }

    /**
     * 根据医生ID查询诊疗记录列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalRecord>> getByDoctorId(@PathVariable Long doctorId) {
        return ResultUtils.success(medicalRecordService.getByDoctorId(doctorId));
    }

    /**
     * 根据病历号查询诊疗记录
     */
    @GetMapping("/no/{recordNo}")
    public Result<MedicalRecord> getByRecordNo(@PathVariable String recordNo) {
        return ResultUtils.success(medicalRecordService.getByRecordNo(recordNo));
    }
}
