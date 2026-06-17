package com.campus.medical.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 实现生成令牌、验证令牌、解析用户 ID 和角色
 * 符合文档第 12.3.10 节 JwtUtils 的要求
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 访问令牌过期时间（秒）
     */
    @Value("${jwt.access-token-expire}")
    private Long accessTokenExpire;

    /**
     * 刷新令牌过期时间（秒）
     */
    @Value("${jwt.refresh-token-expire}")
    private Long refreshTokenExpire;

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT 令牌
     * @param userId 用户 ID
     * @param role 用户角色
     * @return JWT 令牌
     */
    public String generateToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, accessTokenExpire);
    }

    /**
     * 生成刷新令牌
     * @param userId 用户 ID
     * @param role 用户角色
     * @return 刷新令牌
     */
    public String generateRefreshToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, refreshTokenExpire);
    }

    /**
     * 创建令牌
     * @param claims 载荷
     * @param expireTime 过期时间（秒）
     * @return JWT 令牌
     */
    private String createToken(Map<String, Object> claims, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime * 1000);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 验证 JWT 令牌有效性
     * @param token JWT 令牌
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户 ID
     * @param token JWT 令牌
     * @return 用户 ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    /**
     * 从令牌中获取用户角色
     * @param token JWT 令牌
     * @return 用户角色
     */
    public String getUserRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    /**
     * 从令牌中获取过期时间
     * @param token JWT 令牌
     * @return 过期时间
     */
    public Date getExpireDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 获取 Claims
     * @param token JWT 令牌
     * @return Claims
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 JWT 令牌失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析Token，返回用户信息Map
     * @param token JWT令牌
     * @return 包含userId、role等信息的Map
     */
    public Map<String, Object> parseToken(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            throw new RuntimeException("Token无效或已过期");
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", claims.get("userId"));
        userInfo.put("role", claims.get("role"));
        userInfo.put("username", claims.get("username"));
        return userInfo;
    }
}
