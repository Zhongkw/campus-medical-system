package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.VisitStatService;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 就诊统计控制器
 */
@RestController
@RequestMapping("/api/leader/stat")
public class VisitStatController {
    
    @Autowired
    private VisitStatService visitStatService;
    
    /**
     * 获取就诊趋势信息
     */
    @GetMapping("/visitTrend")
    public Result<List<VisitTrendVO>> getVisitTrend(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<VisitTrendVO> result = visitStatService.getVisitTrendInfo(startTime, endTime);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取科室就诊排行
     */
    @GetMapping("/deptVisitRank")
    public Result<List<DeptVisitVO>> getDeptVisitRank() {
        List<DeptVisitVO> result = visitStatService.getDeptVisitRank();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取人群分布
     */
    @GetMapping("/userTypeVisitDist")
    public Result<List<UserTypeVisitVO>> getUserTypeVisitDist() {
        List<UserTypeVisitVO> result = visitStatService.getUserTypeVisitDist();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取就诊高峰分析
     */
    @GetMapping("/visitPeak")
    public Result<List<PeakVisitVO>> getVisitPeak() {
        List<PeakVisitVO> result = visitStatService.getVisitPeakAnalysis();
        return ResultUtils.success(result);
    }
}
