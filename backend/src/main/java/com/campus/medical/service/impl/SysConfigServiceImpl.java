package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysConfig;
import com.campus.medical.mapper.SysConfigMapper;
import com.campus.medical.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务实现类
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    /**
     * 根据配置键获取配置值
     */
    @Override
    public SysConfig getByConfigKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        return baseMapper.selectOne(wrapper);
    }
}
