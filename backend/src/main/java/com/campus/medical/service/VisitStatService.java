package com.campus.medical.service;

import com.campus.medical.vo.*;

import java.util.List;

/**
 * 就诊统计服务接口
 */
public interface VisitStatService {
    
    /**
     * 获取就诊趋势信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 就诊趋势列表
     */
    List<VisitTrendVO> getVisitTrendInfo(String startTime, String endTime);
    
    /**
     * 获取科室就诊排行
     * @return 科室就诊排行列表
     */
    List<DeptVisitVO> getDeptVisitRank();
    
    /**
     * 获取人群分布
     * @return 人群分布列表
     */
    List<UserTypeVisitVO> getUserTypeVisitDist();
    
    /**
     * 获取就诊高峰分析
     * @return 高峰时段列表
     */
    List<PeakVisitVO> getVisitPeakAnalysis();
}
