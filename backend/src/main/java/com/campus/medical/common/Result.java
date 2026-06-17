package com.campus.medical.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 包含 code、message、data 三个字段
 * 符合文档第 12 章要求的统一 JSON 返回格式
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     * 200 - 成功
     * 401 - 未授权
     * 403 - 禁止访问
     * 404 - 资源不存在
     * 500 - 服务器内部错误
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造函数
     */
    private Result() {
    }

    /**
     * 带参数的构造函数
     */
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
