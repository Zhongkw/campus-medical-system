package com.campus.medical.service;

import com.campus.medical.entity.SysConfig;
import com.campus.medical.service.IBaseService;

/**
 * 系统配置服务接口
 */
public interface SysConfigService extends IBaseService<SysConfig> {

    /**
     * 根据配置键获取配置值
     */
    SysConfig getByConfigKey(String configKey);
}
