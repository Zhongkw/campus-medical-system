package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户表 Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}