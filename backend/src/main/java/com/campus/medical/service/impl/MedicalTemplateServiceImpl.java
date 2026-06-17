package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalTemplate;
import com.campus.medical.mapper.MedicalTemplateMapper;
import com.campus.medical.service.MedicalTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 病历模板服务实现类
 */
@Service
public class MedicalTemplateServiceImpl extends BaseServiceImpl<MedicalTemplateMapper, MedicalTemplate> implements MedicalTemplateService {

    /**
     * 根据模板类型查询模板列表
     */
    @Override
    public List<MedicalTemplate> getByTemplateType(String templateType) {
        LambdaQueryWrapper<MedicalTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalTemplate::getTemplateType, templateType);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询模板列表
     */
    @Override
    public List<MedicalTemplate> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalTemplate::getDoctorId, doctorId);
        return baseMapper.selectList(wrapper);
    }
}
