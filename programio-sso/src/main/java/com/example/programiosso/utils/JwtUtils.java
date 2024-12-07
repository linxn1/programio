package com.example.programiosso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 该工具类是用来生成和解析JWT
 * 由于JWT使用的地方较多，相同代码重复编写，代码冗余
 */
@Slf4j
@Component
public class JwtUtils {
    // 指定秘钥，至少三位以上，秘钥如果太短，容易被破解
    private static final String SECRET_KEY = "111111"; // 请替换为你自己的密钥
    // 指定有效期，以分钟为单位，24小时有效期
    private static final Long EXPIRED_IN_MINUTE = 24L * 60; // 24小时有效期

    // 私有化无参构造方法，避免外部随意创建对象
    private JwtUtils() {
    }

    // 生成JWT
    public static String generate(Integer userAccount) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRED_IN_MINUTE * 60 * 1000);

        // JWT的组成
        String jwt = Jwts.builder()
                .setHeaderParam("typ", "jwt")  // 指定类型
                .setHeaderParam("alg", "HS256") // 指定使用的算法
                .claim("userAccount", userAccount)  // 设置用户账号
                .setExpiration(expirationDate)  // 指定有效期
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 指定算法与秘钥
                .compact(); // 打包
        log.info("Generated JWT: {}", jwt);
        return jwt;
    }

    // 解析JWT
    public static Claims parse(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)  // 设置密钥
                    .parseClaimsJws(jwt)  // 解析JWT
                    .getBody();
            return claims;
        } catch (Exception e) {
            log.error("Error parsing JWT", e);
            return null;
        }
    }

    // 获取用户账号
    public static Integer getUserAccountFromToken(String token) {
        Claims claims = parse(token);
        if (claims != null) {
            return (Integer) claims.get("userAccount");
        }
        return null;
    }

    // 验证Token是否过期
    public static boolean isTokenExpired(String token) {
        Claims claims = parse(token);
        if (claims != null) {
            return claims.getExpiration().before(new Date());
        }
        return true;  // 如果解析失败，认为过期
    }

}
