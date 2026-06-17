package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalQueue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 候诊队列表 Mapper 接口
 */
@Mapper
public interface MedicalQueueMapper extends BaseMapper<MedicalQueue> {
}