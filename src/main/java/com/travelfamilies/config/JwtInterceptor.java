package com.travelfamilies.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.travelfamilies.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {

                token = token.substring(7);
            }

            try {

                DecodedJWT decodedJWT = JwtUtils.verifyToken(token);

                request.setAttribute("userID", decodedJWT.getClaim("userID").asInt());

                return true;
            }catch(Exception e) {

                throw new BusinessException("Token 校验失败，请重新登录");
            }
    }
}