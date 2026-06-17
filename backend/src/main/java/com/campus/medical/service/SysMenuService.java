package com.campus.medical.service;

import com.campus.medical.entity.SysMenu;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface SysMenuService extends IBaseService<SysMenu> {

    /**
     * 根据角色ID查询菜单列表
     */
    List<SysMenu> getByRoleId(Long roleId);
}
