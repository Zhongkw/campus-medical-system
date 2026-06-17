package com.campus.medical.service;

import com.campus.medical.vo.*;

import java.util.List;

/**
 * 财务统计服务接口
 */
public interface FinanceStatService {
    
    /**
     * 获取时间段营收数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日营收列表
     */
    List<DailyIncomeVO> getRangeIncomeData(String startTime, String endTime);
    
    /**
     * 获取科室营收统计
     * @return 科室营收列表
     */
    List<DeptIncomeVO> getDepartmentIncomeStat();
    
    /**
     * 获取收入结构占比
     * @return 收入占比列表
     */
    List<IncomeRatioVO> getIncomeStructureRatio();
    
    /**
     * 获取整体财务汇总
     * @return 财务汇总
     */
    FinanceSummaryVO getWholeFinanceSummary();

    /**
     * 按日期获取对账数据
     */
    ReconciliationVO getDailyReconciliation(String date);
}
