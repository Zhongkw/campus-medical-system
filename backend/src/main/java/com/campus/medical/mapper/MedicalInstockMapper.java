package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalInstock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品入库表 Mapper 接口
 */
@Mapper
public interface MedicalInstockMapper extends BaseMapper<MedicalInstock> {
}