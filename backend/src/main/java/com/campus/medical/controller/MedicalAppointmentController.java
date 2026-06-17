package com.campus.medical.controller;

import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/api/medical/appointment")
public class MedicalAppointmentController extends BaseController<MedicalAppointment> {

    @Autowired
    private MedicalAppointmentService medicalAppointmentService;

    @Override
    protected IBaseService<MedicalAppointment> getService() {
        return medicalAppointmentService;
    }

    /**
     * 根据患者ID查询预约列表
     */
    @GetMapping("/patient/{patientId}")
    public List<MedicalAppointment> getByPatientId(@PathVariable Long patientId) {
        return medicalAppointmentService.getByPatientId(patientId);
    }

    /**
     * 根据医生ID查询预约列表
     */
    @GetMapping("/doctor/{doctorId}")
    public List<MedicalAppointment> getByDoctorId(@PathVariable Long doctorId) {
        return medicalAppointmentService.getByDoctorId(doctorId);
    }

    /**
     * 根据预约编号查询预约
     */
    @GetMapping("/no/{appointmentNo}")
    public MedicalAppointment getByAppointmentNo(@PathVariable String appointmentNo) {
        return medicalAppointmentService.getByAppointmentNo(appointmentNo);
    }
}
