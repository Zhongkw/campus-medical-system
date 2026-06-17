package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电子病历表 Mapper 接口
 */
@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {
}