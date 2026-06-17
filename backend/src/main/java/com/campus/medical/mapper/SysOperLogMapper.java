package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统操作日志表 Mapper 接口
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}