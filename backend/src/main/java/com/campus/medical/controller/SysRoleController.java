package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.SysRole;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/system/role")
public class SysRoleController extends BaseController<SysRole> {

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    protected IBaseService<SysRole> getService() {
        return sysRoleService;
    }

    /**
     * 根据角色编码查询角色
     */
    @GetMapping("/code/{roleCode}")
    public Result<SysRole> getByRoleCode(@PathVariable String roleCode) {
        return ResultUtils.success(sysRoleService.getByRoleCode(roleCode));
    }
}
