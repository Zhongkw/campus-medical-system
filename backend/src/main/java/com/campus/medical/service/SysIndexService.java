package com.campus.medical.service;

import com.campus.medical.vo.SysIndexStatVO;

/**
 * 系统首页服务接口
 */
public interface SysIndexService {
    
    /**
     * 获取首页统计数据
     * @return 首页统计
     */
    SysIndexStatVO getIndexStatData();
}
