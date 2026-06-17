package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalDepartment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科室表 Mapper 接口
 */
@Mapper
public interface MedicalDepartmentMapper extends BaseMapper<MedicalDepartment> {
}