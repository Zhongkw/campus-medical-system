package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysMenu;
import com.campus.medical.mapper.SysMenuMapper;
import com.campus.medical.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单服务实现类
 */
@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /**
     * 根据角色ID查询菜单列表
     */
    @Override
    public List<SysMenu> getByRoleId(Long roleId) {
        // TODO: 需要在SysMenuMapper中添加selectMenusByRoleId方法
        // 暂时返回空列表
        return List.of();
    }
}
