package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.mapper.MedicalMedicineMapper;
import com.campus.medical.service.MedicalMedicineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 药品服务实现类
 */
@Service
public class MedicalMedicineServiceImpl extends BaseServiceImpl<MedicalMedicineMapper, MedicalMedicine> implements MedicalMedicineService {

    /**
     * 根据药品编码查询药品
     */
    @Override
    public MedicalMedicine getByMedicineCode(String medicineCode) {
        // MedicalMedicine实体类没有medicineCode字段，使用medicineName
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalMedicine::getMedicineName, medicineCode);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据药品名称模糊查询药品列表
     */
    @Override
    public List<MedicalMedicine> getByMedicineName(String medicineName) {
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(MedicalMedicine::getMedicineName, medicineName);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据药品类别查询药品列表
     */
    @Override
    public List<MedicalMedicine> getByCategory(String category) {
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalMedicine::getCategory, category);
        return baseMapper.selectList(wrapper);
    }
}
