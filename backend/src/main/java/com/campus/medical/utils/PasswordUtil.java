package com.campus.medical.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 使用BCrypt算法，替代不安全的MD5
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 对明文密码进行BCrypt加密
     * @param rawPassword 明文密码
     * @return BCrypt加密后的密文（以$2a$开头）
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证明文密码与密文是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword BCrypt加密后的密文
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断密码是否为BCrypt格式
     * @param encodedPassword 密文
     * @return 是否为BCrypt格式
     */
    public static boolean isBCryptFormat(String encodedPassword) {
        return encodedPassword != null && (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }
}
