package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统登录日志表 Mapper 接口
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}