package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.HealthWarnService;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康预警控制器
 */
@RestController
@RequestMapping("/api/leader/warn")
public class HealthWarnController {
    
    @Autowired
    private HealthWarnService healthWarnService;
    
    /**
     * 扫描异常健康数据
     */
    @GetMapping("/abnormalHealth")
    public Result<List<AbnormalDiseaseVO>> getAbnormalHealth() {
        List<AbnormalDiseaseVO> result = healthWarnService.scanCurrentHealthAbnormal();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取预警记录列表
     */
    @GetMapping("/warnRecordList")
    public Result<List<WarnHistoryVO>> getWarnRecordList() {
        List<WarnHistoryVO> result = healthWarnService.getWarnRecordList();
        return ResultUtils.success(result);
    }
    
    /**
     * 设置预警阈值
     */
    @PostMapping("/setThreshold")
    public Result<Boolean> setThreshold(@RequestParam Integer num) {
        Boolean result = healthWarnService.setWarnThreshold(num);
        return ResultUtils.success(result);
    }
}
