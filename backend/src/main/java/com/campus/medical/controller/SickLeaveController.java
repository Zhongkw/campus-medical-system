package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 病假申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/edu")
public class SickLeaveController {
    
    /**
     * 提交病假申请
     */
    @PostMapping("/syncSickLeave")
    public Result<Map<String, Object>> syncSickLeave(@RequestBody Map<String, Object> requestBody) {
        Integer recordId = (Integer) requestBody.get("recordId");
        String startDate = (String) requestBody.get("startDate");
        String endDate = (String) requestBody.get("endDate");
        Integer days = (Integer) requestBody.get("days");
        String reason = (String) requestBody.get("reason");
        
        log.info("提交病假申请: recordId={}, startDate={}, endDate={}, days={}, reason={}", 
                recordId, startDate, endDate, days, reason);
        
        // 模拟提交成功
        Map<String, Object> result = new HashMap<>();
        result.put("id", System.currentTimeMillis());
        result.put("applyNo", "SL" + System.currentTimeMillis());
        result.put("status", "待审核");
        result.put("message", "提交成功，等待审批");
        
        return ResultUtils.success("提交成功，等待审批", result);
    }
}