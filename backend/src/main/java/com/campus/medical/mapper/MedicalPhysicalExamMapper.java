package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalPhysicalExam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体检记录表 Mapper 接口
 */
@Mapper
public interface MedicalPhysicalExamMapper extends BaseMapper<MedicalPhysicalExam> {
}