package com.campus.medical.service;

import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 审批服务接口
 */
public interface MedicalApprovalService extends IBaseService<MedicalApproval> {

    /**
     * 根据审批编号查询审批
     */
    MedicalApproval getByApprovalNo(String approvalNo);

    /**
     * 根据申请人ID查询审批列表
     */
    List<MedicalApproval> getByApplicantId(Long applicantId);

    /**
     * 根据状态查询审批列表
     */
    List<MedicalApproval> getByStatus(String status);
}
