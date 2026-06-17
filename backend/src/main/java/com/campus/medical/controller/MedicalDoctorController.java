package com.campus.medical.controller;

import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 医生控制器
 */
@RestController
@RequestMapping("/api/medical/doctor")
public class MedicalDoctorController extends BaseController<MedicalDoctor> {

    @Autowired
    private MedicalDoctorService doctorService;

    @Override
    protected IBaseService<MedicalDoctor> getService() {
        return doctorService;
    }
}
