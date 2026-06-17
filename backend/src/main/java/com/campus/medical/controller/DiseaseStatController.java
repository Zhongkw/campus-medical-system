package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.DiseaseStatService;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疾病统计控制器
 */
@RestController
@RequestMapping("/api/leader/stat")
public class DiseaseStatController {
    
    @Autowired
    private DiseaseStatService diseaseStatService;
    
    /**
     * 获取疾病排行榜
     */
    @GetMapping("/diseaseTop10")
    public Result<List<DiseaseRankVO>> getDiseaseTop10(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<DiseaseRankVO> result = diseaseStatService.getTopDiseaseRank(startTime, endTime);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取单个疾病趋势
     */
    @GetMapping("/diseaseTrend")
    public Result<List<DiseaseTrendVO>> getDiseaseTrend(@RequestParam String diseaseName) {
        List<DiseaseTrendVO> result = diseaseStatService.getSingleDiseaseTrend(diseaseName);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取学院疾病分布
     */
    @GetMapping("/collegeDiseaseDist")
    public Result<List<CollegeDiseaseVO>> getCollegeDiseaseDist() {
        List<CollegeDiseaseVO> result = diseaseStatService.getCollegeDiseaseDistData();
        return ResultUtils.success(result);
    }
}
