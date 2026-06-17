package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalHealthProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人健康档案表 Mapper 接口
 */
@Mapper
public interface MedicalHealthProfileMapper extends BaseMapper<MedicalHealthProfile> {
}