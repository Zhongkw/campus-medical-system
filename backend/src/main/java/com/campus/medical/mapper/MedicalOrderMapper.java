package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表 Mapper 接口
 */
@Mapper
public interface MedicalOrderMapper extends BaseMapper<MedicalOrder> {
}