package com.travelfamilies.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.travelfamilies.response.HandleResponse;
import com.travelfamilies.response.Result;
import com.travelfamilies.tools.RedisConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;

    public JwtAuthenticationFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);
        try {

            DecodedJWT decodedJWT = JwtUtils.verifyToken(token);
            int userId = decodedJWT.getClaim("userID").asInt();
            int roleId = decodedJWT.getClaim("roleID").asInt();

            String redisToken = roleId == 1 ? stringRedisTemplate.opsForValue().get(RedisConstant.USER_TOKEN + userId)
                    : stringRedisTemplate.opsForValue().get(RedisConstant.ADMIN_TOKEN + userId);



            Boolean blackStatus=roleId==1? stringRedisTemplate.hasKey(RedisConstant.USER_BLACK_LIST)
                                :stringRedisTemplate.hasKey(RedisConstant.ADMIN_BLACK_LIST);
            if(blackStatus){
                String message= "该账号异常,禁止登录";
                HandleResponse.createResponse(403,message,response);
                return;
            }


            if (!redisToken.equals(token)) {

                SecurityContextHolder.clearContext();

                HandleResponse.createResponse(401, "登陆已过期或在别处登录，请重新登录",response);
                return;
            }


            String username = decodedJWT.getClaim("username").asString();


            request.setAttribute("userID", userId);
            request.setAttribute("roleID", roleId);
            request.setAttribute("username", username);

            String roleName = (roleId == 1) ? "ROLE_USER" : "ROLE_ADMIN";
            // 构造权限列表
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}