package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysRoleMenu;
import com.campus.medical.mapper.SysRoleMenuMapper;
import com.campus.medical.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色菜单服务实现类
 */
@Service
public class SysRoleMenuServiceImpl extends BaseServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    /**
     * 根据角色ID删除角色菜单关联
     */
    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        baseMapper.delete(wrapper);
    }
}
