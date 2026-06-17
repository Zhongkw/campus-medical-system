package com.campus.medical.service;

import com.campus.medical.dto.LoginDTO;
import com.campus.medical.dto.LoginVO;
import com.campus.medical.dto.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录响应（包含Token）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新Token
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    String refreshToken(String refreshToken);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);
}
