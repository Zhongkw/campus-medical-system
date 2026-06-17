package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.SickLeaveAuditRequestDTO;
import com.campus.medical.dto.SickLeaveRequestDTO;
import com.campus.medical.entity.*;
import com.campus.medical.mapper.*;
import com.campus.medical.service.SickLeaveBusinessService;
import com.campus.medical.vo.CollegeSickStatVO;
import com.campus.medical.vo.SickLeaveDetailVO;
import com.campus.medical.vo.SickLeaveSummaryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 病假申请审批业务服务实现类
 */
@Service
public class SickLeaveBusinessServiceImpl implements SickLeaveBusinessService {

    private static final Logger log = LoggerFactory.getLogger(SickLeaveBusinessServiceImpl.class);

    @Autowired
    private MedicalSickLeaveMapper sickLeaveMapper;

    @Autowired
    private MedicalSickNoteMapper sickNoteMapper;

    @Autowired
    private MedicalApprovalMapper approvalMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private MedicalRecordMapper recordMapper;

    @Autowired
    private MedicalOrderMapper orderMapper;

    // ========== 学生端：病假申请 ==========

    /**
     * 提交病假申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitSickLeave(SickLeaveRequestDTO requestDTO, Long userId) {
        // 1. 校验请假天数（结束日期不能早于开始日期）
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        // 2. 计算请假天数
        long days = ChronoUnit.DAYS.between(requestDTO.getStartDate(), requestDTO.getEndDate()) + 1;
        
        // 3. 校验请假天数（单次请假最多7天）
        if (days > 7) {
            throw new RuntimeException("单次请假天数最多不超过7天");
        }

        // 4. 生成病假申请编号（SL+日期+序号）
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String leaveNo = "SL" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);

        // 5. 创建病假申请记录
        MedicalSickLeave sickLeave = new MedicalSickLeave();
        sickLeave.setLeaveNo(leaveNo);
        sickLeave.setUserId(userId);
        sickLeave.setDiagnosis(requestDTO.getDiagnosis());
        sickLeave.setStartDate(requestDTO.getStartDate());
        sickLeave.setEndDate(requestDTO.getEndDate());
        sickLeave.setLeaveDays((int) days);
        sickLeave.setDiagnosisProofUrl(requestDTO.getDiagnosisProofUrl());
        sickLeave.setDoctorId(0L);
        sickLeave.setSickNoteId(0L);
        sickLeave.setStatus(1);
        sickLeaveMapper.insert(sickLeave);

        log.info("病假申请提交成功，申请编号：{}，用户ID：{}，请假天数：{}", leaveNo, userId, days);
        return leaveNo;
    }

    /**
     * 查询我的病假申请列表
     */
    @Override
    public List<MedicalSickLeave> getMySickLeaves(Long userId, String status) {
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper();
        wrapper.eq(MedicalSickLeave::getUserId, userId);
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalSickLeave::getStatus, getStatusValue(status));
        }
        
        wrapper.orderByDesc(MedicalSickLeave::getCreateTime);
        return sickLeaveMapper.selectList(wrapper);
    }

    /**
     * 查询病假申请详情
     */
    @Override
    public SickLeaveDetailVO getSickLeaveDetail(Long applyId, Long userId) {
        // 1. 查询病假申请信息
        MedicalSickLeave sickLeave = sickLeaveMapper.selectById(applyId);
        if (sickLeave == null) {
            throw new RuntimeException("病假申请不存在");
        }

        // 2. 验证权限（只能查看自己的申请）
        if (!sickLeave.getUserId().equals(userId)) {
            throw new RuntimeException("无权限访问");
        }

        // 3. 组装详情VO
        SickLeaveDetailVO detailVO = new SickLeaveDetailVO();
        detailVO.setId(sickLeave.getId());
        detailVO.setLeaveNo(sickLeave.getLeaveNo());
        detailVO.setDiagnosis(sickLeave.getDiagnosis());
        detailVO.setStartDate(sickLeave.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        detailVO.setEndDate(sickLeave.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        detailVO.setLeaveDays(sickLeave.getLeaveDays());
        detailVO.setDiagnosisProofUrl(sickLeave.getDiagnosisProofUrl());
        detailVO.setStatus(convertSickLeaveStatusToString(sickLeave.getStatus()));
        detailVO.setAuditContent(sickLeave.getAuditContent());
        detailVO.setAuditTime(sickLeave.getAuditTime() != null ? 
                sickLeave.getAuditTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        detailVO.setCreateTime(sickLeave.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 4. 查询学生信息
        SysUser user = userMapper.selectById(userId);
        if (user != null) {
            detailVO.setStudentName(user.getRealName());
            detailVO.setStudentId(user.getUsername());
            detailVO.setCollege(user.getDepartment());
        }

        return detailVO;
    }

    /**
     * 撤回病假申请（仅待审核状态可撤回）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawSickLeave(Long applyId, Long userId) {
        // 1. 查询病假申请信息
        MedicalSickLeave sickLeave = sickLeaveMapper.selectById(applyId);
        if (sickLeave == null) {
            throw new RuntimeException("病假申请不存在");
        }

        // 2. 验证权限
        if (!sickLeave.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        // 3. 校验状态（只有待审核状态可撤回）
        if (sickLeave.getStatus() != 1) {
            throw new RuntimeException("只有待审核状态的申请才能撤回");
        }

        // 4. 更新状态为已撤回
        sickLeave.setStatus(4);
        sickLeaveMapper.updateById(sickLeave);

        log.info("病假申请撤回成功，申请编号：{}", sickLeave.getLeaveNo());
        return true;
    }

    // ========== 医生端：开具病假条 ==========

    /**
     * 医生开具病假条
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createSickNote(Long recordId, Long userId, Long doctorId, 
                                String diagnosis, LocalDate startDate, LocalDate endDate) {
        // 1. 校验请假天数
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        // 2. 计算请假天数
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 3. 校验请假天数（最多7天）
        if (days > 7) {
            throw new RuntimeException("单次请假天数最多不超过7天");
        }

        // 4. 生成病假条编号（SN+日期+序号）
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sickNoteNo = "SN" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);

        // 5. 创建病假条记录
        MedicalSickNote sickNote = new MedicalSickNote();
        sickNote.setSickNoteNo(sickNoteNo);
        sickNote.setRecordId(recordId);
        sickNote.setUserId(userId);
        sickNote.setDoctorId(doctorId);
        sickNote.setDiagnosis(diagnosis);
        sickNote.setStartDate(startDate);
        sickNote.setEndDate(endDate);
        sickNote.setLeaveDays((int) days);
        sickNoteMapper.insert(sickNote);

        // 6. 同步创建病假申请记录（状态为已通过）
        String leaveNo = "SL" + dateStr + String.format("%04d", (System.currentTimeMillis() % 10000) + 1000);
        MedicalSickLeave sickLeave = new MedicalSickLeave();
        sickLeave.setLeaveNo(leaveNo);
        sickLeave.setUserId(userId);
        sickLeave.setDoctorId(doctorId);
        sickLeave.setSickNoteId(sickNote.getId());
        sickLeave.setDiagnosis(diagnosis);
        sickLeave.setStartDate(startDate);
        sickLeave.setEndDate(endDate);
        sickLeave.setLeaveDays((int) days);
        sickLeave.setStatus(2);
        sickLeaveMapper.insert(sickLeave);

        log.info("病假条开具成功，病假条编号：{}，患者ID：{}，请假天数：{}", sickNoteNo, userId, days);
        return sickNoteNo;
    }

    /**
     * 查询医生开具的病假条列表
     */
    @Override
    public List<MedicalSickNote> getDoctorSickNotes(Long doctorId) {
        LambdaQueryWrapper<MedicalSickNote> wrapper = new LambdaQueryWrapper();
        wrapper.eq(MedicalSickNote::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalSickNote::getCreateTime);
        return sickNoteMapper.selectList(wrapper);
    }

    // ========== 审批端：病假申请审批 ==========

    /**
     * 查询病假申请列表（审批端）
     */
    @Override
    public List<SickLeaveDetailVO> getSickLeaveListForApproval(String college, String status) {
        // 1. 查询病假申请列表
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalSickLeave::getStatus, getStatusValue(status));
        }
        
        wrapper.orderByDesc(MedicalSickLeave::getCreateTime);
        List<MedicalSickLeave> sickLeaves = sickLeaveMapper.selectList(wrapper);

        // 2. 组装详情VO列表
        List<SickLeaveDetailVO> result = new ArrayList<>();
        for (MedicalSickLeave sickLeave : sickLeaves) {
            // 查询学生信息
            SysUser user = userMapper.selectById(sickLeave.getUserId());
            if (user == null) {
                continue;
            }

            // 如果指定了学院筛选，过滤不符合的申请
            if (college != null && !college.isEmpty() && !college.equals(user.getDepartment())) {
                continue;
            }

            SickLeaveDetailVO vo = new SickLeaveDetailVO();
            vo.setId(sickLeave.getId());
            vo.setLeaveNo(sickLeave.getLeaveNo());
            vo.setStudentName(user.getRealName());
            vo.setStudentId(user.getUsername());
            vo.setCollege(user.getDepartment());
            vo.setDiagnosis(sickLeave.getDiagnosis());
            vo.setStartDate(sickLeave.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setEndDate(sickLeave.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setLeaveDays(sickLeave.getLeaveDays());
            vo.setStatus(convertSickLeaveStatusToString(sickLeave.getStatus()));
            vo.setAuditContent(sickLeave.getAuditContent());
            vo.setCreateTime(sickLeave.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.add(vo);
        }

        return result;
    }

    /**
     * 审批病假申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSickLeave(SickLeaveAuditRequestDTO requestDTO) {
        // 1. 查询病假申请信息
        MedicalSickLeave sickLeave = sickLeaveMapper.selectById(requestDTO.getApplyId());
        if (sickLeave == null) {
            throw new RuntimeException("病假申请不存在");
        }

        // 2. 校验状态（只有待审核状态可审批）
        if (sickLeave.getStatus() != 1) {
            throw new RuntimeException("只有待审核状态的申请才能审批");
        }

        // 3. 审核处理
        sickLeave.setAuditorId(requestDTO.getAuditorId());
        sickLeave.setAuditContent(requestDTO.getAuditContent());
        sickLeave.setUpdateTime(LocalDateTime.now());

        if ("PASS".equals(requestDTO.getAuditStatus())) {
            // 审核通过
            sickLeave.setStatus(2);
            log.info("病假申请审核通过，申请编号：{}，审核人：{}", sickLeave.getLeaveNo(), requestDTO.getAuditorId());
            
            // TODO: 审批通过后同步至教务系统（Mock接口）
        } else if ("REJECT".equals(requestDTO.getAuditStatus())) {
            // 审核驳回
            sickLeave.setStatus(3);
            log.info("病假申请审核驳回，申请编号：{}，审核人：{}，原因：{}", 
                    sickLeave.getLeaveNo(), requestDTO.getAuditorId(), requestDTO.getAuditContent());
        } else {
            throw new RuntimeException("审核状态异常");
        }

        sickLeaveMapper.updateById(sickLeave);
        return true;
    }

    /**
     * 批量审批病假申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAuditSickLeave(List<Long> applyIds, String auditStatus, 
                                      String auditContent, Long auditorId) {
        for (Long applyId : applyIds) {
            try {
                SickLeaveAuditRequestDTO requestDTO = new SickLeaveAuditRequestDTO();
                requestDTO.setApplyId(applyId);
                requestDTO.setAuditStatus(auditStatus);
                requestDTO.setAuditContent(auditContent);
                requestDTO.setAuditorId(auditorId);
                auditSickLeave(requestDTO);
            } catch (Exception e) {
                log.error("批量审批失败，申请ID：{}，错误：{}", applyId, e.getMessage());
                // 继续处理下一个
            }
        }
        return true;
    }

    /**
     * 统计各学院病假申请数量
     */
    @Override
    public List<CollegeSickStatVO> getCollegeSickStat() {
        List<MedicalSickLeave> allSickLeaves = sickLeaveMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, CollegeSickStatVO> collegeMap = new LinkedHashMap<>();
        int totalCount = 0;

        for (MedicalSickLeave sickLeave : allSickLeaves) {
            SysUser user = userMapper.selectById(sickLeave.getUserId());
            if (user == null || user.getDepartment() == null) {
                continue;
            }
            String college = user.getDepartment();
            CollegeSickStatVO vo = collegeMap.computeIfAbsent(college, key -> {
                CollegeSickStatVO item = new CollegeSickStatVO();
                item.setCollege(key);
                item.setApplyCount(0L);
                item.setApprovedCount(0L);
                item.setRejectedCount(0L);
                item.setPendingCount(0L);
                return item;
            });
            vo.setApplyCount(vo.getApplyCount() + 1);
            if (sickLeave.getStatus() != null) {
                if (sickLeave.getStatus() == 2) {
                    vo.setApprovedCount(vo.getApprovedCount() + 1);
                } else if (sickLeave.getStatus() == 3) {
                    vo.setRejectedCount(vo.getRejectedCount() + 1);
                } else if (sickLeave.getStatus() == 1) {
                    vo.setPendingCount(vo.getPendingCount() + 1);
                }
            }
            totalCount++;
        }

        List<CollegeSickStatVO> result = new ArrayList<>(collegeMap.values());
        for (CollegeSickStatVO vo : result) {
            vo.setPercentage(totalCount > 0 ? (vo.getApplyCount().doubleValue() * 100.0 / totalCount) : 0.0);
        }
        result.sort((a, b) -> Long.compare(b.getApplyCount(), a.getApplyCount()));
        return result;
    }

    @Override
    public SickLeaveSummaryVO getSickLeaveSummary() {
        List<MedicalSickLeave> allSickLeaves = sickLeaveMapper.selectList(new LambdaQueryWrapper<>());
        SickLeaveSummaryVO summary = new SickLeaveSummaryVO();
        summary.setTotal((long) allSickLeaves.size());
        long approved = 0;
        long rejected = 0;
        long pending = 0;
        for (MedicalSickLeave sickLeave : allSickLeaves) {
            if (sickLeave.getStatus() == null) {
                continue;
            }
            if (sickLeave.getStatus() == 2) {
                approved++;
            } else if (sickLeave.getStatus() == 3) {
                rejected++;
            } else if (sickLeave.getStatus() == 1) {
                pending++;
            }
        }
        summary.setApproved(approved);
        summary.setRejected(rejected);
        summary.setPending(pending);
        return summary;
    }

    // ========== 特殊挂号/退费审批 ==========

    /**
     * 提交特殊挂号申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitSpecialAppointmentApproval(Long scheduleId, Long applicantId, String reason) {
        // 1. 生成审批编号（AP+日期+序号）
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String approvalNo = "AP" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);

        // 2. 创建审批记录
        MedicalApproval approval = new MedicalApproval();
        approval.setApprovalNo(approvalNo);
        approval.setApprovalType("特殊挂号");
        approval.setBusinessId(scheduleId);
        approval.setApplicantId(applicantId);
        approval.setReason(reason);
        approval.setStatus("待审核");
        approvalMapper.insert(approval);

        log.info("特殊挂号申请提交成功，审批编号：{}，申请人ID：{}", approvalNo, applicantId);
        return approvalNo;
    }

    /**
     * 提交退费申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitRefundApproval(Long orderId, Long applicantId, String reason) {
        MedicalOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(applicantId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!"已支付".equals(order.getStatus())) {
            throw new RuntimeException("仅已缴费订单可申请退费");
        }

        LambdaQueryWrapper<MedicalApproval> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(MedicalApproval::getApprovalType, "退费");
        pendingWrapper.eq(MedicalApproval::getBusinessId, orderId);
        pendingWrapper.eq(MedicalApproval::getApplicantId, applicantId);
        pendingWrapper.eq(MedicalApproval::getStatus, "待审核");
        if (approvalMapper.selectCount(pendingWrapper) > 0) {
            throw new RuntimeException("该订单已有待审核的退费申请");
        }

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String approvalNo = "RF" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);

        SysUser student = userMapper.selectById(applicantId);
        MedicalApproval approval = new MedicalApproval();
        approval.setApprovalNo(approvalNo);
        approval.setApprovalType("退费");
        approval.setBusinessId(orderId);
        approval.setApplicantId(applicantId);
        approval.setReason(reason);
        approval.setStatus("待审核");
        approval.setPatientName(student != null ? student.getRealName() : "");
        approval.setPatientNo(student != null ? student.getUsername() : "");
        approval.setRefundAmount(order.getTotalAmount());
        approvalMapper.insert(approval);

        log.info("退费申请提交成功，审批编号：{}，申请人ID：{}", approvalNo, applicantId);
        return approvalNo;
    }

    /**
     * 审批业务申请（特殊挂号/退费）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditApproval(String approvalNo, String auditStatus, 
                                String auditContent, Long auditorId) {
        // 1. 查询审批记录
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getApprovalNo, approvalNo);
        MedicalApproval approval = approvalMapper.selectOne(wrapper);
        
        if (approval == null) {
            throw new RuntimeException("审批记录不存在");
        }

        // 2. 校验状态
        if (!"待审核".equals(approval.getStatus())) {
            throw new RuntimeException("只有待审核状态的申请才能审批");
        }

        // 3. 审核处理
        approval.setAuditorId(auditorId);
        approval.setAuditContent(auditContent);
        approval.setUpdateTime(LocalDateTime.now());

        if ("PASS".equals(auditStatus)) {
            approval.setStatus("已通过");
            log.info("审批通过，审批编号：{}，审核人：{}", approvalNo, auditorId);

            if ("退费".equals(approval.getApprovalType())) {
                MedicalOrder order = orderMapper.selectById(approval.getBusinessId());
                if (order == null) {
                    throw new RuntimeException("关联订单不存在");
                }
                if (!"已支付".equals(order.getStatus())) {
                    throw new RuntimeException("订单状态异常，无法退费");
                }
                order.setStatus("已退费");
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(order);
                log.info("退费审批通过，订单号：{}", order.getOrderNo());
            }

        } else if ("REJECT".equals(auditStatus)) {
            approval.setStatus("已驳回");
            log.info("审批驳回，审批编号：{}，审核人：{}，原因：{}", approvalNo, auditorId, auditContent);
        } else {
            throw new RuntimeException("审核状态异常");
        }

        approvalMapper.updateById(approval);
        return true;
    }

    /**
     * 查询待审批列表
     */
    @Override
    public List<MedicalApproval> getApprovalList(String approvalType, String status) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        
        if (approvalType != null && !approvalType.isEmpty()) {
            wrapper.eq(MedicalApproval::getApprovalType, approvalType);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalApproval::getStatus, status);
        }
        
        wrapper.orderByDesc(MedicalApproval::getCreateTime);
        return approvalMapper.selectList(wrapper);
    }

    @Override
    public List<MedicalApproval> getMyRefundApplications(Long userId) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getApprovalType, "退费");
        wrapper.eq(MedicalApproval::getApplicantId, userId);
        wrapper.orderByDesc(MedicalApproval::getCreateTime);
        return approvalMapper.selectList(wrapper);
    }

    private Integer getStatusValue(String status) {
        if (status == null) return null;
        switch (status) {
            case "待审核": return 1;
            case "已通过": return 2;
            case "已驳回": return 3;
            case "已撤回": return 4;
            default: return null;
        }
    }

    private String convertSickLeaveStatusToString(Integer status) {
        if (status == null) return null;
        switch (status) {
            case 1: return "待审核";
            case 2: return "已通过";
            case 3: return "已驳回";
            case 4: return "已撤回";
            default: return "未知";
        }
    }
}
