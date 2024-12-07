package com.example.programiosso.filter;

import com.example.programiocommon.pojo.dto.UserTokenDTO;
import com.example.programiosso.mapper.user.UserTokenMapper;
import com.example.programiosso.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 单点登录验证authorization

    // 引入 JwtUtils 来解析和验证 JWT
    private final JwtUtils jwtUtils;

    @Autowired
    private UserTokenMapper userTokenMapper;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        // 如果没有 Authorization 头，或者不是以 "Bearer " 开头，则跳过该过滤器
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 提取 JWT 令牌
        String jwtToken = authorizationHeader.substring(7); // 去掉 "Bearer " 前缀
        Claims claims = jwtUtils.parse(jwtToken); // 解析 JWT 获取用户信息


        if (claims != null) {
            Integer userAccount = (Integer) claims.get("userAccount");// 获取存储在 JWT 中的用户信息
            // 创建一个认证对象
            if (userAccount != null) {
                // 通过用户名加载用户信息（假设你有一个 UserDetailsService 来提供用户数据）
                UserTokenDTO userToken = userTokenMapper.findUserByAccount(userAccount);
                System.out.println(userToken);
                Timestamp createdAt = userToken.getCreatedAt();
                Timestamp expiresAt = userToken.getExpiresAt();
                Timestamp currentTime = new Timestamp(System.currentTimeMillis()); // 获取当前时间

                System.out.println(createdAt);
                System.out.println(expiresAt);
                System.out.println(currentTime);
                // 判断当前时间是否在有效期范围内
                if (currentTime.after(createdAt) && currentTime.before(expiresAt)) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new ProgramIOAuthenticationToken(userToken)
                    );
                } else {
                    // 如果用户不在有效时间范围内，清除 SecurityContext 中的认证信息
                    SecurityContextHolder.clearContext();
                }
            }
        }
        // 继续执行过滤链
        filterChain.doFilter(request, response);
    }
}

