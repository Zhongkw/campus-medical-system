package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.DrugConsumeService;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品消耗控制器
 */
@RestController
@RequestMapping("/api/leader/stat")
public class DrugConsumeController {
    
    @Autowired
    private DrugConsumeService drugConsumeService;
    
    /**
     * 获取药品消耗排行
     */
    @GetMapping("/drugConsumeRank")
    public Result<List<DrugConsumeVO>> getDrugConsumeRank(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<DrugConsumeVO> result = drugConsumeService.getDrugUseRank(startTime, endTime);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取药品类型占比
     */
    @GetMapping("/drugClassRatio")
    public Result<List<DrugTypeRatioVO>> getDrugClassRatio() {
        List<DrugTypeRatioVO> result = drugConsumeService.getDrugClassRatio();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取补货需求
     */
    @GetMapping("/drugReplenish")
    public Result<List<DrugReplenishVO>> getDrugReplenish() {
        List<DrugReplenishVO> result = drugConsumeService.calcDrugReplenishNeed();
        return ResultUtils.success(result);
    }
}
