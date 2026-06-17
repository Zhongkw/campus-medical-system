package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalInvoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发票表 Mapper 接口
 */
@Mapper
public interface MedicalInvoiceMapper extends BaseMapper<MedicalInvoice> {
}