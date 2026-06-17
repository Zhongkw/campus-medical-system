package com.campus.medical.service;

import com.campus.medical.entity.SysRole;
import com.campus.medical.service.IBaseService;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IBaseService<SysRole> {

    /**
     * 根据角色编码查询角色
     */
    SysRole getByRoleCode(String roleCode);
}
