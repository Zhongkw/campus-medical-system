package com.campus.medical.service;

import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 药品服务接口
 */
public interface MedicalMedicineService extends IBaseService<MedicalMedicine> {

    /**
     * 根据药品编码查询药品
     */
    MedicalMedicine getByMedicineCode(String medicineCode);

    /**
     * 根据药品名称模糊查询药品列表
     */
    List<MedicalMedicine> getByMedicineName(String medicineName);

    /**
     * 根据药品类别查询药品列表
     */
    List<MedicalMedicine> getByCategory(String category);
}
