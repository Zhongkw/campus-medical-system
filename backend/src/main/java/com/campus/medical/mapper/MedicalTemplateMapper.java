package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病历模板表 Mapper 接口
 */
@Mapper
public interface MedicalTemplateMapper extends BaseMapper<MedicalTemplate> {
}