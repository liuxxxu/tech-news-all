package com.liuxu.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类
 */
public class AppJwtUtil {
    // TOKEN的有效期时间7天
    private static final int TOKEN_TIME_OUT = 60 * 60 * 24 * 7 * 1000;
    // 加密密钥 (Base64编码的256位密钥)
    private static final String TOKEN_SECRET_KEY = "technewssecretkey12345678901234567890";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 300;

    // 密钥实例
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(TOKEN_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    /**
     * 生成token
     * 
     * @param id 用户id
     * @return token字符串
     */
    public static String getToken(Long id) {
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id", id);
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(currentTime)) // 签发时间
                .subject("system") // 说明
                .issuer("tech-news") // 签发者信息
                .audience().add("app").and() // 接收用户
                .expiration(new Date(currentTime + TOKEN_TIME_OUT)) // 过期时间戳
                .claims(claimMaps) // claims信息
                .signWith(SECRET_KEY) // 加密方式
                .compact();
    }

    /**
     * 解析token中的claims信息
     *
     * @param token JWT token
     * @return Claims对象
     */
    private static Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
    }

    /**
     * 获取payload body信息
     *
     * @param token JWT token
     * @return Claims对象，过期返回null
     */
    public static Claims getClaimsBody(String token) {
        try {
            return parseToken(token).getPayload();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 获取header信息
     *
     * @param token JWT token
     * @return Header对象
     */
    public static Header getHeaderBody(String token) {
        return parseToken(token).getHeader();
    }

    /**
     * 验证token是否有效
     *
     * @param claims Claims对象
     * @return -1：有效且不需要刷新，0：有效但需要刷新，1：过期，2：异常
     */
    public static int verifyToken(Claims claims) {
        if (claims == null) {
            return 1;
        }

        try {
            long expTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();

            // token已过期
            if (expTime <= currentTime) {
                return 1;
            }

            // 判断是否需要刷新
            if ((expTime - currentTime) > REFRESH_TIME * 1000) {
                return -1; // 不需要刷新
            } else {
                return 0; // 需要刷新
            }
        } catch (ExpiredJwtException ex) {
            return 1;
        } catch (Exception e) {
            return 2;
        }
    }
}
