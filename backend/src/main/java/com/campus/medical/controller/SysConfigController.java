package com.campus.medical.controller;

import com.campus.medical.entity.SysConfig;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/system/config")
public class SysConfigController extends BaseController<SysConfig> {

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    protected IBaseService<SysConfig> getService() {
        return sysConfigService;
    }

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/key/{configKey}")
    public SysConfig getByConfigKey(@PathVariable String configKey) {
        return sysConfigService.getByConfigKey(configKey);
    }
}
