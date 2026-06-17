package com.campus.medical.controller;

import com.campus.medical.entity.SysRoleMenu;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 角色菜单控制器
 */
@RestController
@RequestMapping("/api/system/role-menu")
public class SysRoleMenuController extends BaseController<SysRoleMenu> {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    protected IBaseService<SysRoleMenu> getService() {
        return sysRoleMenuService;
    }

    /**
     * 根据角色ID删除角色菜单关联
     */
    @DeleteMapping("/role/{roleId}")
    public void deleteByRoleId(@PathVariable Long roleId) {
        sysRoleMenuService.deleteByRoleId(roleId);
    }
}
