package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一身份认证Mock接口控制器
 * 模拟校园统一身份认证系统对接
 */
@Slf4j
@RestController
@RequestMapping("/api/mock/auth")
public class AuthMockController {

    /**
     * 统一身份认证登录（Mock）
     * 模拟校园统一身份认证系统
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> unifiedLogin(
            @RequestParam String username,
            @RequestParam String password) {
        
        log.info("调用统一身份认证Mock接口，用户名：{}", username);

        // 模拟认证逻辑
        Map<String, Object> userInfo = new HashMap<>();
        
        // 模拟不同用户角色的返回数据
        if (username.startsWith("stu")) {
            // 学生
            userInfo.put("userId", 1001L);
            userInfo.put("username", username);
            userInfo.put("realName", "张三");
            userInfo.put("roleCode", "STUDENT");
            userInfo.put("roleName", "学生");
            userInfo.put("department", "计算机学院");
            userInfo.put("studentId", username);
        } else if (username.startsWith("doc")) {
            // 医生
            userInfo.put("userId", 2001L);
            userInfo.put("username", username);
            userInfo.put("realName", "李医生");
            userInfo.put("roleCode", "DOCTOR");
            userInfo.put("roleName", "医生");
            userInfo.put("department", "内科");
            userInfo.put("title", "主治医师");
        } else if (username.startsWith("pharm")) {
            // 药师
            userInfo.put("userId", 3001L);
            userInfo.put("username", username);
            userInfo.put("realName", "王药师");
            userInfo.put("roleCode", "PHARMACIST");
            userInfo.put("roleName", "药师");
            userInfo.put("department", "药房");
        } else if (username.startsWith("fin")) {
            // 财务
            userInfo.put("userId", 4001L);
            userInfo.put("username", username);
            userInfo.put("realName", "赵财务");
            userInfo.put("roleCode", "FINANCE");
            userInfo.put("roleName", "财务人员");
            userInfo.put("department", "财务处");
        } else if (username.startsWith("app")) {
            // 审批员
            userInfo.put("userId", 5001L);
            userInfo.put("username", username);
            userInfo.put("realName", "刘辅导员");
            userInfo.put("roleCode", "APPROVER");
            userInfo.put("roleName", "审批管理员");
            userInfo.put("department", "学生处");
        } else if (username.startsWith("lead")) {
            // 校领导
            userInfo.put("userId", 6001L);
            userInfo.put("username", username);
            userInfo.put("realName", "陈校长");
            userInfo.put("roleCode", "LEADER");
            userInfo.put("roleName", "校领导");
            userInfo.put("department", "校办");
        } else if (username.startsWith("admin")) {
            // 管理员
            userInfo.put("userId", 7001L);
            userInfo.put("username", username);
            userInfo.put("realName", "系统管理员");
            userInfo.put("roleCode", "ADMIN");
            userInfo.put("roleName", "系统管理员");
            userInfo.put("department", "信息中心");
        } else {
            return ResultUtils.error("用户名或密码错误");
        }

        // 模拟Token（实际应该调用JWT生成）
        userInfo.put("token", "mock_token_" + username + "_" + System.currentTimeMillis());
        userInfo.put("expiresIn", 7200);

        log.info("统一身份认证Mock登录成功，用户：{}", username);
        return ResultUtils.success("登录成功", userInfo);
    }

    /**
     * 验证Token有效性（Mock）
     */
    @GetMapping("/validateToken")
    public Result<Map<String, Object>> validateToken(@RequestParam String token) {
        log.info("验证Token有效性：{}", token);

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("userId", 1001L);
        result.put("username", "stu2023001");
        result.put("roleCode", "STUDENT");

        return ResultUtils.success(result);
    }

    /**
     * 退出登录（Mock）
     */
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestParam String token) {
        log.info("退出登录，Token：{}", token);
        return ResultUtils.success(true);
    }

    /**
     * 获取用户权限列表（Mock）
     */
    @GetMapping("/getUserPermissions")
    public Result<Map<String, Object>> getUserPermissions(@RequestParam Long userId) {
        log.info("获取用户权限列表，用户ID：{}", userId);

        Map<String, Object> permissions = new HashMap<>();
        permissions.put("userId", userId);
        permissions.put("roleCode", "STUDENT");
        
        // 模拟学生权限
        String[] menus = {
            "dashboard",
            "appointment",
            "payment",
            "records",
            "sickleave",
            "health-profile"
        };
        permissions.put("menus", menus);

        return ResultUtils.success(permissions);
    }
}
