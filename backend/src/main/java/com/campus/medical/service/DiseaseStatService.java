package com.campus.medical.service;

import com.campus.medical.vo.*;

import java.util.List;

/**
 * 疾病统计服务接口
 */
public interface DiseaseStatService {
    
    /**
     * 获取疾病排行榜
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 疾病排行列表
     */
    List<DiseaseRankVO> getTopDiseaseRank(String startTime, String endTime);
    
    /**
     * 获取单个疾病趋势
     * @param diseaseName 疾病名称
     * @return 疾病趋势列表
     */
    List<DiseaseTrendVO> getSingleDiseaseTrend(String diseaseName);
    
    /**
     * 获取学院疾病分布
     * @return 学院疾病分布列表
     */
    List<CollegeDiseaseVO> getCollegeDiseaseDistData();
}
