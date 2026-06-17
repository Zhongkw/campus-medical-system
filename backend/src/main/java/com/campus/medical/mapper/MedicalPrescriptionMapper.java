package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalPrescription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 处方表 Mapper 接口
 */
@Mapper
public interface MedicalPrescriptionMapper extends BaseMapper<MedicalPrescription> {
}