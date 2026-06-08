package com.travelfamilies.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.travelfamilies.response.CodeMessage;
import com.travelfamilies.response.HandleResponse;
import com.travelfamilies.tools.RedisConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(StringRedisTemplate stringRedisTemplate, JwtUtils jwtUtils) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);
        try {

            DecodedJWT decodedJWT = jwtUtils.verifyToken(token);
            long userId = decodedJWT.getClaim("userID").asLong();
            int roleId = decodedJWT.getClaim("roleID").asInt();

            String roleName;
            String key;
            String blackKey;
            if (roleId == 1) {
                key = RedisConstant.USER_TOKEN + userId;
                blackKey = RedisConstant.USER_BLACK_LIST + userId;
                roleName = "ROLE_USER";
            } else if (roleId == 2) {
                key = RedisConstant.ADMIN_TOKEN + userId;
                blackKey = RedisConstant.ADMIN_BLACK_LIST + userId;
                roleName = "ROLE_ADMIN";
            } else {
                key = RedisConstant.HOTEL_ADMIN_TOKEN + userId;
                blackKey = RedisConstant.HOTEL_ADMIN_BLACK_LIST + userId;
                roleName = "ROLE_HOTEL";
            }

            // 1. 先检查黑名单
            Boolean blackStatus = stringRedisTemplate.hasKey(blackKey);
            if (Boolean.TRUE.equals(blackStatus)) {
                HandleResponse.createResponse(CodeMessage.FORBIDDEN.getCode(), CodeMessage.FORBIDDEN.getMessage(), response);
                return;
            }

            // 2. 再校验 token 是否匹配（防重复登录/过期）
            String redisToken = stringRedisTemplate.opsForValue().get(key);
            if (redisToken != null && !redisToken.equals(token)) {
                SecurityContextHolder.clearContext();
                HandleResponse.createResponse(401, "登陆已过期或在别处登录，请重新登录", response);
                return;
            }



            String username = decodedJWT.getClaim("username").asString();


            request.setAttribute("userID", userId);
            request.setAttribute("roleID", roleId);
            request.setAttribute("username", username);

            // 构造权限列表
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {
            log.warn("JWT 认证失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            HandleResponse.createResponse(401, "认证失败，请重新登录", response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}