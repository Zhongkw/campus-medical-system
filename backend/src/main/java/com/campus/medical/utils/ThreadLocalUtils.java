package com.campus.medical.utils;

import java.util.Map;

/**
 * ThreadLocal 工具类
 * 用于存储当前用户信息
 * 符合文档第 12.3.11 节 ThreadLocalUtils 的要求
 */
public class ThreadLocalUtils {

    /**
     * 存储当前用户 ID
     */
    private static final ThreadLocal<String> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存储当前用户信息
     */
    private static final ThreadLocal<Object> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存储通用数据（如Map）
     */
    private static final ThreadLocal<Map<String, Object>> DATA_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     * @param userId 用户 ID
     */
    public static void setCurrentUserId(String userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    /**
     * 获取当前用户 ID
     * @return 用户 ID
     */
    public static String getCurrentUserId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    /**
     * 设置当前用户信息
     * @param user 用户信息
     */
    public static void setCurrentUser(Object user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    public static Object getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 设置数据
     * @param data 数据Map
     */
    public static void set(Map<String, Object> data) {
        DATA_THREAD_LOCAL.set(data);
    }

    /**
     * 获取数据
     * @return 数据Map
     */
    public static Map<String, Object> get() {
        return DATA_THREAD_LOCAL.get();
    }

    /**
     * 清除 ThreadLocal 中的数据
     * 防止内存泄漏，必须在请求结束时调用
     */
    public static void remove() {
        USER_ID_THREAD_LOCAL.remove();
        USER_THREAD_LOCAL.remove();
        DATA_THREAD_LOCAL.remove();
    }
}
