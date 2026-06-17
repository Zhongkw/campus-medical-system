package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.SickApplyQueryDTO;
import com.campus.medical.service.CancelRegApproveService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 退号申请审批控制器
 */
@RestController
@RequestMapping("/api/approve/cancelReg")
public class CancelRegApproveController {
    
    @Autowired
    private CancelRegApproveService cancelRegApproveService;
    
    /**
     * 查询退号申请列表
     */
    @GetMapping("/list")
    public Result<PageResultVO<CancelRegVO>> getList(SickApplyQueryDTO queryDTO) {
        PageResultVO<CancelRegVO> result = cancelRegApproveService.queryCancelApplyList(queryDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 审核退号申请
     */
    @PostMapping("/audit")
    public Result<Boolean> audit(
            @RequestParam Long cancelId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String remark) {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long auditorId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        Boolean result = cancelRegApproveService.doCancelAudit(cancelId, auditStatus, remark, auditorId);
        return ResultUtils.success(result);
    }
}
