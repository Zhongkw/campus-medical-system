package com.campus.medical.interceptor;

import com.campus.medical.utils.JwtUtils;
import com.campus.medical.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * JWT拦截器
 * 用于验证用户登录状态和权限控制
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 预处理方法，在Controller之前执行
     * 验证JWT令牌的有效性
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检请求不需要验证Token）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 获取请求头中的Token
        String token = request.getHeader("Authorization");

        // Token为空，返回未授权
        if (token == null || token.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录，请先登录");
            return false;
        }

        // 验证Token格式
        if (!token.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token格式错误，必须以Bearer开头");
            return false;
        }

        // 去除Bearer前缀
        token = token.substring(7);

        try {
            // 验证Token并解析用户信息
            Map<String, Object> claims = jwtUtils.parseToken(token);

            // 将用户信息存入ThreadLocal，供后续使用
            ThreadLocalUtils.set(claims);

            // 放行
            return true;
        } catch (Exception e) {
            // Token无效或过期
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期，请重新登录");
            return false;
        }
    }

    /**
     * 后处理方法，在Controller之后执行
     * 清理ThreadLocal，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        ThreadLocalUtils.remove();
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", status, message));
    }
}

