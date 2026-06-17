package com.campus.medical.controller;

import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品控制器
 */
@RestController
@RequestMapping("/api/medical/medicine")
public class MedicalMedicineController extends BaseController<MedicalMedicine> {

    @Autowired
    private MedicalMedicineService medicalMedicineService;

    @Override
    protected IBaseService<MedicalMedicine> getService() {
        return medicalMedicineService;
    }

    /**
     * 根据药品编码查询药品
     */
    @GetMapping("/code/{medicineCode}")
    public MedicalMedicine getByMedicineCode(@PathVariable String medicineCode) {
        return medicalMedicineService.getByMedicineCode(medicineCode);
    }

    /**
     * 根据药品名称模糊查询药品列表
     */
    @GetMapping("/search")
    public List<MedicalMedicine> getByMedicineName(@RequestParam String medicineName) {
        return medicalMedicineService.getByMedicineName(medicineName);
    }

    /**
     * 根据药品类别查询药品列表
     */
    @GetMapping("/category/{category}")
    public List<MedicalMedicine> getByCategory(@PathVariable String category) {
        return medicalMedicineService.getByCategory(category);
    }
}
