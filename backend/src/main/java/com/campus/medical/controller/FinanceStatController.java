package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.FinanceStatService;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务统计控制器
 */
@RestController
@RequestMapping("/api/finance/stat")
public class FinanceStatController {
    
    @Autowired
    private FinanceStatService financeStatService;
    
    /**
     * 获取时间段营收数据
     */
    @GetMapping("/rangeIncome")
    public Result<List<DailyIncomeVO>> getRangeIncome(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<DailyIncomeVO> result = financeStatService.getRangeIncomeData(startTime, endTime);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取科室营收统计
     */
    @GetMapping("/deptIncome")
    public Result<List<DeptIncomeVO>> getDeptIncome() {
        List<DeptIncomeVO> result = financeStatService.getDepartmentIncomeStat();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取收入结构占比
     */
    @GetMapping("/incomeRatio")
    public Result<List<IncomeRatioVO>> getIncomeRatio() {
        List<IncomeRatioVO> result = financeStatService.getIncomeStructureRatio();
        return ResultUtils.success(result);
    }
    
    /**
     * 获取整体财务汇总
     */
    @GetMapping("/summary")
    public Result<FinanceSummaryVO> getSummary() {
        FinanceSummaryVO result = financeStatService.getWholeFinanceSummary();
        return ResultUtils.success(result);
    }

    /**
     * 按日期对账
     */
    @GetMapping("/reconciliation")
    public Result<ReconciliationVO> getReconciliation(@RequestParam(required = false) String date) {
        return ResultUtils.success(financeStatService.getDailyReconciliation(date));
    }
}
