package com.campus.medical.controller;

import com.campus.medical.entity.SysOperLog;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/system/oper-log")
public class SysOperLogController extends BaseController<SysOperLog> {

    @Autowired
    private SysOperLogService sysOperLogService;

    @Override
    protected IBaseService<SysOperLog> getService() {
        return sysOperLogService;
    }
}
