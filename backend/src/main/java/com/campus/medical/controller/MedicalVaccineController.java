package com.campus.medical.controller;

import com.campus.medical.entity.MedicalVaccine;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalVaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疫苗接种记录控制器
 */
@RestController
@RequestMapping("/api/medical/vaccine")
public class MedicalVaccineController extends BaseController<MedicalVaccine> {

    @Autowired
    private MedicalVaccineService medicalVaccineService;

    @Override
    protected IBaseService<MedicalVaccine> getService() {
        return medicalVaccineService;
    }

    /**
     * 根据用户ID查询疫苗接种记录列表
     */
    @GetMapping("/user/{userId}")
    public List<MedicalVaccine> getByUserId(@PathVariable Long userId) {
        return medicalVaccineService.getByUserId(userId);
    }
}
