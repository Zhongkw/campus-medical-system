package com.campus.medical.controller;

import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 科室控制器
 */
@RestController
@RequestMapping("/api/medical/department")
public class MedicalDepartmentController extends BaseController<MedicalDepartment> {

    @Autowired
    private MedicalDepartmentService departmentService;

    @Override
    protected IBaseService<MedicalDepartment> getService() {
        return departmentService;
    }
}
