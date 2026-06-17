package com.campus.medical.service;

import com.campus.medical.entity.MedicalTemplate;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 病历模板服务接口
 */
public interface MedicalTemplateService extends IBaseService<MedicalTemplate> {

    /**
     * 根据模板类型查询模板列表
     */
    List<MedicalTemplate> getByTemplateType(String templateType);

    /**
     * 根据医生ID查询模板列表
     */
    List<MedicalTemplate> getByDoctorId(Long doctorId);
}
