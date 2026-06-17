package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalSickLeave;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电子病假申请表 Mapper 接口
 */
@Mapper
public interface MedicalSickLeaveMapper extends BaseMapper<MedicalSickLeave> {
}