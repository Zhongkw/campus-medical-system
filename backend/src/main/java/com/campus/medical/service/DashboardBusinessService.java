package com.campus.medical.service;

import com.campus.medical.vo.DashboardVO;

/**
 * 工作台业务服务接口
 */
public interface DashboardBusinessService {

    /**
     * 获取工作台核心数据（根据角色动态返回）
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 工作台数据
     */
    DashboardVO getDashboardData(Long userId, String roleCode);

    /**
     * 获取待办事项列表
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 待办事项列表
     */
    java.util.List<DashboardVO.TodoItemVO> getTodoList(Long userId, String roleCode);

    /**
     * 获取已办事项列表
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 已办事项列表
     */
    java.util.List<DashboardVO.DoneItemVO> getDoneList(Long userId, String roleCode);
}
