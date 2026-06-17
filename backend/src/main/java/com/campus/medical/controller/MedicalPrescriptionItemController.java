package com.campus.medical.controller;

import com.campus.medical.entity.MedicalPrescriptionItem;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalPrescriptionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处方明细控制器
 */
@RestController
@RequestMapping("/api/medical/prescription-item")
public class MedicalPrescriptionItemController extends BaseController<MedicalPrescriptionItem> {

    @Autowired
    private MedicalPrescriptionItemService medicalPrescriptionItemService;

    @Override
    protected IBaseService<MedicalPrescriptionItem> getService() {
        return medicalPrescriptionItemService;
    }

    /**
     * 根据处方ID查询处方明细列表
     */
    @GetMapping("/prescription/{prescriptionId}")
    public List<MedicalPrescriptionItem> getByPrescriptionId(@PathVariable Long prescriptionId) {
        return medicalPrescriptionItemService.getByPrescriptionId(prescriptionId);
    }
}
