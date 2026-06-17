package com.campus.medical.controller;

import com.campus.medical.entity.MedicalTemplate;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病历模板控制器
 */
@RestController
@RequestMapping("/api/medical/template")
public class MedicalTemplateController extends BaseController<MedicalTemplate> {

    @Autowired
    private MedicalTemplateService medicalTemplateService;

    @Override
    protected IBaseService<MedicalTemplate> getService() {
        return medicalTemplateService;
    }

    /**
     * 根据模板类型查询模板列表
     */
    @GetMapping("/type/{templateType}")
    public List<MedicalTemplate> getByTemplateType(@PathVariable String templateType) {
        return medicalTemplateService.getByTemplateType(templateType);
    }

    /**
     * 根据医生ID查询模板列表
     */
    @GetMapping("/doctor/{doctorId}")
    public List<MedicalTemplate> getByDoctorId(@PathVariable Long doctorId) {
        return medicalTemplateService.getByDoctorId(doctorId);
    }
}
