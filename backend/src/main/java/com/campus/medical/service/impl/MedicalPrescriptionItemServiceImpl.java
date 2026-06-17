package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalPrescriptionItem;
import com.campus.medical.mapper.MedicalPrescriptionItemMapper;
import com.campus.medical.service.MedicalPrescriptionItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 处方明细服务实现类
 */
@Service
public class MedicalPrescriptionItemServiceImpl extends BaseServiceImpl<MedicalPrescriptionItemMapper, MedicalPrescriptionItem> implements MedicalPrescriptionItemService {

    /**
     * 根据处方ID查询处方明细列表
     */
    @Override
    public List<MedicalPrescriptionItem> getByPrescriptionId(Long prescriptionId) {
        LambdaQueryWrapper<MedicalPrescriptionItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescriptionItem::getPrescriptionId, prescriptionId);
        return baseMapper.selectList(wrapper);
    }
}
