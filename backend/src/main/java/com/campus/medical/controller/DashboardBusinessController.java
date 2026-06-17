package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.DashboardBusinessService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作台业务控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardBusinessController {

    @Autowired
    private DashboardBusinessService dashboardBusinessService;

    /**
     * 获取工作台核心数据
     * @return 工作台数据
     */
    @GetMapping("/getData")
    public Result<DashboardVO> getDashboardData() {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long userId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        String roleCode = String.valueOf(currentUser.get("role"));
        DashboardVO dashboard = dashboardBusinessService.getDashboardData(userId, roleCode);
        return ResultUtils.success(dashboard);
    }

    /**
     * 获取待办事项列表
     * @return 待办事项列表
     */
    @GetMapping("/getTodoList")
    public Result<List<DashboardVO.TodoItemVO>> getTodoList() {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long userId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        String roleCode = String.valueOf(currentUser.get("role"));
        List<DashboardVO.TodoItemVO> todoList = dashboardBusinessService.getTodoList(userId, roleCode);
        return ResultUtils.success(todoList);
    }

    /**
     * 获取已办事项列表
     * @return 已办事项列表
     */
    @GetMapping("/getDoneList")
    public Result<List<DashboardVO.DoneItemVO>> getDoneList() {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long userId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        String roleCode = String.valueOf(currentUser.get("role"));
        List<DashboardVO.DoneItemVO> doneList = dashboardBusinessService.getDoneList(userId, roleCode);
        return ResultUtils.success(doneList);
    }
}
