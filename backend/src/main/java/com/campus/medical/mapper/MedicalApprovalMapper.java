package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalApproval;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批表 Mapper 接口
 */
@Mapper
public interface MedicalApprovalMapper extends BaseMapper<MedicalApproval> {
}