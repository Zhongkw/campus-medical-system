package com.campus.medical.common;

/**
 * 统一响应工具类
 * 实现 success、error、unauthorized、forbidden 等方法
 * 符合文档第 12.3.1 节 ResultUtils 的要求
 * code=0 表示成功
 */
public class ResultUtils {

    /**
     * 返回成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    /**
     * 返回成功响应（带数据）
     * @param data 响应数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    /**
     * 返回成功响应（带消息和数据）
     * @param message 响应消息
     * @param data 响应数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    /**
     * 返回错误响应
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 返回自定义错误码的错误响应
     * @param code 错误码
     * @param message 错误消息
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 返回未授权响应
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权，请先登录", null);
    }

    /**
     * 返回未授权响应（带消息）
     * @param message 错误消息
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /**
     * 返回禁止访问响应
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问", null);
    }

    /**
     * 返回禁止访问响应（带消息）
     * @param message 错误消息
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    /**
     * 返回资源不存在响应
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在", null);
    }

    /**
     * 返回资源不存在响应（带消息）
     * @param message 错误消息
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
}
