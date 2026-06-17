package com.campus.medical.service.impl;

import com.campus.medical.entity.SysOperLog;
import com.campus.medical.mapper.SysOperLogMapper;
import com.campus.medical.service.SysOperLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 */
@Service
public class SysOperLogServiceImpl extends BaseServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
}
