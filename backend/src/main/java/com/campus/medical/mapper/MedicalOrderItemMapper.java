package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细表 Mapper 接口
 */
@Mapper
public interface MedicalOrderItemMapper extends BaseMapper<MedicalOrderItem> {
}