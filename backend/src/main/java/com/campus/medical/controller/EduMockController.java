package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教务系统Mock控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mock/edu")
public class EduMockController {
    
    /**
     * 模拟同步请假信息到教务系统
     */
    @PostMapping("/syncLeave")
    public Result<Map<String, Object>> syncLeave(
            @RequestParam String studentNo,
            @RequestParam String leaveNo,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String reason) {
        log.info("同步请假信息到教务系统: studentNo={}, leaveNo={}, startDate={}, endDate={}",
            studentNo, leaveNo, startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("syncStatus", "SUCCESS");
        result.put("eduLeaveNo", "EDU" + System.currentTimeMillis());
        result.put("message", "请假信息同步成功");
        
        return ResultUtils.success(result);
    }
    
    /**
     * 模拟查询教务系统请假记录
     */
    @GetMapping("/queryLeave")
    public Result<List<Map<String, Object>>> queryLeave(@RequestParam String studentNo) {
        log.info("查询教务系统请假记录: studentNo={}", studentNo);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 模拟返回一些请假记录
        Map<String, Object> record1 = new HashMap<>();
        record1.put("leaveNo", "EDU20240101001");
        record1.put("studentNo", studentNo);
        record1.put("startDate", "2024-01-01");
        record1.put("endDate", "2024-01-03");
        record1.put("reason", "病假");
        record1.put("status", "APPROVED");
        result.add(record1);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("leaveNo", "EDU20240115001");
        record2.put("studentNo", studentNo);
        record2.put("startDate", "2024-01-15");
        record2.put("endDate", "2024-01-15");
        record2.put("reason", "事假");
        record2.put("status", "APPROVED");
        result.add(record2);
        
        return ResultUtils.success(result);
    }
}
