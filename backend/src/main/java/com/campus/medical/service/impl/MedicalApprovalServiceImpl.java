package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.mapper.MedicalApprovalMapper;
import com.campus.medical.service.MedicalApprovalService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审批服务实现类
 */
@Service
public class MedicalApprovalServiceImpl extends BaseServiceImpl<MedicalApprovalMapper, MedicalApproval> implements MedicalApprovalService {

    /**
     * 根据审批编号查询审批
     */
    @Override
    public MedicalApproval getByApprovalNo(String approvalNo) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getApprovalNo, approvalNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据申请人ID查询审批列表
     */
    @Override
    public List<MedicalApproval> getByApplicantId(Long applicantId) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getApplicantId, applicantId);
        wrapper.orderByDesc(MedicalApproval::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据状态查询审批列表
     */
    @Override
    public List<MedicalApproval> getByStatus(String status) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getStatus, status);
        wrapper.orderByDesc(MedicalApproval::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}
