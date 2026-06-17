package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.service.SickApplyApproveService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 病假申请审批控制器
 */
@RestController
@RequestMapping("/api/approve/sick")
public class SickApplyApproveController {
    
    @Autowired
    private SickApplyApproveService sickApplyApproveService;
    
    /**
     * 分页查询病假申请
     */
    @GetMapping("/pageList")
    public Result<PageResultVO<SickApplyVO>> pageList(SickApplyQueryDTO queryDTO) {
        PageResultVO<SickApplyVO> result = sickApplyApproveService.pageQuerySickApply(queryDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取申请详情
     */
    @GetMapping("/detail/{applyId}")
    public Result<SickApplyDetailVO> getDetail(@PathVariable Long applyId) {
        SickApplyDetailVO detail = sickApplyApproveService.getApplyDetail(applyId);
        return ResultUtils.success(detail);
    }
    
    /**
     * 审核病假申请
     */
    @PostMapping("/doAudit")
    public Result<Boolean> doAudit(
            @RequestParam Long applyId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String auditContent) {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long auditorId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        Boolean result = sickApplyApproveService.doSickApplyAudit(applyId, auditStatus, auditContent, auditorId);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取学院病假统计
     */
    @GetMapping("/collegeStat")
    public Result<List<CollegeSickStatVO>> getCollegeStat() {
        List<CollegeSickStatVO> result = sickApplyApproveService.getCollegeSickStatData();
        return ResultUtils.success(result);
    }
}
