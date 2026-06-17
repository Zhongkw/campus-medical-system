package com.campus.medical.controller;

import com.campus.medical.entity.SysMenu;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController extends BaseController<SysMenu> {

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    protected IBaseService<SysMenu> getService() {
        return sysMenuService;
    }

    /**
     * 根据角色ID查询菜单列表
     */
    @GetMapping("/role/{roleId}")
    public List<SysMenu> getByRoleId(@PathVariable Long roleId) {
        return sysMenuService.getByRoleId(roleId);
    }
}
