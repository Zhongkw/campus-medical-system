package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalDoctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生表 Mapper 接口
 */
@Mapper
public interface MedicalDoctorMapper extends BaseMapper<MedicalDoctor> {
}