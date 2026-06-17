package com.campus.medical.config;

import com.campus.medical.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web拦截器配置类
 * 配置JWT拦截器，实现登录认证和权限控制
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        "/api/auth/login",           // 登录接口
                        "/api/auth/register",         // 注册接口
                        "/error",                     // 错误页面
                        "/static/**",                 // 静态资源
                        "/swagger-ui/**",             // Swagger文档
                        "/v3/api-docs/**"             // API文档
                );
    }
}
