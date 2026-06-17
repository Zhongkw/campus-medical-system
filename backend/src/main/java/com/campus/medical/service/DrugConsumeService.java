package com.campus.medical.service;

import com.campus.medical.vo.*;

import java.util.List;

/**
 * 药品消耗统计服务接口
 */
public interface DrugConsumeService {
    
    /**
     * 获取药品消耗排行
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 药品消耗排行列表
     */
    List<DrugConsumeVO> getDrugUseRank(String startTime, String endTime);
    
    /**
     * 获取药品类型占比
     * @return 药品类型占比列表
     */
    List<DrugTypeRatioVO> getDrugClassRatio();
    
    /**
     * 计算补货需求
     * @return 补货需求列表
     */
    List<DrugReplenishVO> calcDrugReplenishNeed();
}
