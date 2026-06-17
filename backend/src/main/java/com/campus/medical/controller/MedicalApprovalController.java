package com.campus.medical.controller;

import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批控制器
 */
@RestController
@RequestMapping("/api/medical/approval")
public class MedicalApprovalController extends BaseController<MedicalApproval> {

    @Autowired
    private MedicalApprovalService medicalApprovalService;

    @Override
    protected IBaseService<MedicalApproval> getService() {
        return medicalApprovalService;
    }

    /**
     * 根据审批编号查询审批
     */
    @GetMapping("/no/{approvalNo}")
    public MedicalApproval getByApprovalNo(@PathVariable String approvalNo) {
        return medicalApprovalService.getByApprovalNo(approvalNo);
    }

    /**
     * 根据申请人ID查询审批列表
     */
    @GetMapping("/applicant/{applicantId}")
    public List<MedicalApproval> getByApplicantId(@PathVariable Long applicantId) {
        return medicalApprovalService.getByApplicantId(applicantId);
    }

    /**
     * 根据状态查询审批列表
     */
    @GetMapping("/status/{status}")
    public List<MedicalApproval> getByStatus(@PathVariable String status) {
        return medicalApprovalService.getByStatus(status);
    }
}
