package com.campus.medical.service;

import com.campus.medical.vo.*;

import java.util.List;

/**
 * 健康预警服务接口
 */
public interface HealthWarnService {
    
    /**
     * 扫描当前健康异常
     * @return 异常疾病列表
     */
    List<AbnormalDiseaseVO> scanCurrentHealthAbnormal();
    
    /**
     * 获取预警记录列表
     * @return 预警历史列表
     */
    List<WarnHistoryVO> getWarnRecordList();
    
    /**
     * 设置预警阈值
     * @param num 阈值
     * @return 是否成功
     */
    Boolean setWarnThreshold(Integer num);
}
