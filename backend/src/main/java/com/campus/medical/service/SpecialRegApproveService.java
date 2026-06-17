package com.campus.medical.service;

import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.vo.*;

import java.util.List;

/**
 * 特殊挂号审批服务接口
 */
public interface SpecialRegApproveService {
    
    /**
     * 查询特殊挂号列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResultVO<SpecialRegVO> querySpecialRegList(SickApplyQueryDTO queryDTO);
    
    /**
     * 获取挂号详情
     * @param regId 挂号ID
     * @return 挂号详情
     */
    SpecialRegDetailVO getRegDetail(Long regId);
    
    /**
     * 审核特殊挂号
     * @param regId 挂号ID
     * @param auditStatus 审核状态
     * @param remark 审核备注
     * @param auditorId 审核人ID
     * @return 是否成功
     */
    Boolean auditSpecialReg(Long regId, String auditStatus, String remark, Long auditorId);
}
