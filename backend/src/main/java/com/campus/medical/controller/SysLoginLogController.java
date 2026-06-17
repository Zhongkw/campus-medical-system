package com.campus.medical.controller;

import com.campus.medical.entity.SysLoginLog;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志控制器
 */
@RestController
@RequestMapping("/api/system/login-log")
public class SysLoginLogController extends BaseController<SysLoginLog> {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Override
    protected IBaseService<SysLoginLog> getService() {
        return sysLoginLogService;
    }
}
