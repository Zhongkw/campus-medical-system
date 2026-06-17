package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalVaccine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 疫苗接种记录表 Mapper 接口
 */
@Mapper
public interface MedicalVaccineMapper extends BaseMapper<MedicalVaccine> {
}