package com.campus.medical.service;

import com.campus.medical.entity.SysRoleMenu;
import com.campus.medical.service.IBaseService;

/**
 * 角色菜单服务接口
 */
public interface SysRoleMenuService extends IBaseService<SysRoleMenu> {

    /**
     * 根据角色ID删除角色菜单关联
     */
    void deleteByRoleId(Long roleId);
}
