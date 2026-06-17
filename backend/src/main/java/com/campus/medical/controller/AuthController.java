package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.LoginDTO;
import com.campus.medical.dto.LoginVO;
import com.campus.medical.dto.UserInfoVO;
import com.campus.medical.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录、登出、刷新Token等接口
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录响应（包含Token）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = authService.login(loginDTO);
            return ResultUtils.success("登录成功", loginVO);
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage());
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        try {
            authService.logout();
            return ResultUtils.success("登出成功", null);
        } catch (Exception e) {
            log.error("登出失败：{}", e.getMessage());
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 刷新Token
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestParam String refreshToken) {
        try {
            String newAccessToken = authService.refreshToken(refreshToken);
            return ResultUtils.success("刷新成功", newAccessToken);
        } catch (Exception e) {
            log.error("刷新Token失败：{}", e.getMessage());
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     * @param requestBody 请求体（包含userId）
     * @return 用户信息
     */
    @PostMapping("/getUserInfo")
    public Result<UserInfoVO> getUserInfo(@RequestBody java.util.Map<String, Long> requestBody) {
        try {
            Long userId = requestBody.get("userId");
            UserInfoVO userInfo = authService.getUserInfo(userId);
            return ResultUtils.success("获取成功", userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败：{}", e.getMessage());
            return ResultUtils.error(e.getMessage());
        }
    }
}
