package com.zzl.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;


    //taken有效期
    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    角色列表
     * @return JWT
     */
    public String generateToken(Long userId, String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 Token，返回 Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 Token 是否有效（未过期、签名正确）
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && claims.getExpiration().after(new Date());
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? Long.parseLong(claims.getSubject()) : null;
    }
}