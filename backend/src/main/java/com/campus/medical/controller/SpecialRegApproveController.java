package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.service.SpecialRegApproveService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 特殊挂号审批控制器
 */
@RestController
@RequestMapping("/api/approve/specialReg")
public class SpecialRegApproveController {
    
    @Autowired
    private SpecialRegApproveService specialRegApproveService;
    
    /**
     * 查询特殊挂号列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<SpecialRegVO>> getList(SickApplyQueryDTO queryDTO) {
        PageResultVO<SpecialRegVO> result = specialRegApproveService.querySpecialRegList(queryDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取挂号详情
     */
    @GetMapping("/detail/{regId}")
    public Result<SpecialRegDetailVO> getDetail(@PathVariable Long regId) {
        SpecialRegDetailVO detail = specialRegApproveService.getRegDetail(regId);
        return ResultUtils.success(detail);
    }
    
    /**
     * 审核特殊挂号
     */
    @PostMapping("/audit")
    public Result<Boolean> audit(
            @RequestParam Long regId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String remark) {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long auditorId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        Boolean result = specialRegApproveService.auditSpecialReg(regId, auditStatus, remark, auditorId);
        return ResultUtils.success(result);
    }
}
