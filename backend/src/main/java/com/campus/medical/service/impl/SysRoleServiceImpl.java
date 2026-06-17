package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysRole;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    /**
     * 根据角色编码查询角色
     */
    @Override
    public SysRole getByRoleCode(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        return baseMapper.selectOne(wrapper);
    }
}
