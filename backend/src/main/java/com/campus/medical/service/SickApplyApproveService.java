package com.campus.medical.service;

import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.vo.*;

import java.util.List;

/**
 * 病假申请审批服务接口
 */
public interface SickApplyApproveService {
    
    /**
     * 分页查询病假申请
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResultVO<SickApplyVO> pageQuerySickApply(SickApplyQueryDTO queryDTO);
    
    /**
     * 获取申请详情
     * @param applyId 申请ID
     * @return 申请详情
     */
    SickApplyDetailVO getApplyDetail(Long applyId);
    
    /**
     * 审核病假申请
     * @param applyId 申请ID
     * @param auditStatus 审核状态
     * @param auditContent 审核内容
     * @param auditorId 审核人ID
     * @return 是否成功
     */
    Boolean doSickApplyAudit(Long applyId, String auditStatus, String auditContent, Long auditorId);
    
    /**
     * 获取学院病假统计
     * @return 学院病假统计列表
     */
    List<CollegeSickStatVO> getCollegeSickStatData();
}
