package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生排班表 Mapper 接口
 */
@Mapper
public interface MedicalScheduleMapper extends BaseMapper<MedicalSchedule> {
}