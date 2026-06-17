package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalAppointment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约表 Mapper 接口
 */
@Mapper
public interface MedicalAppointmentMapper extends BaseMapper<MedicalAppointment> {
}