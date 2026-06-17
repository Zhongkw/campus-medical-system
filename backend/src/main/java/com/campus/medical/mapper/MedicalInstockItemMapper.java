package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalInstockItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品入库明细表 Mapper 接口
 */
@Mapper
public interface MedicalInstockItemMapper extends BaseMapper<MedicalInstockItem> {
}