package com.travelfamilies.config;

import com.travelfamilies.response.HandleResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException) throws IOException{

        HandleResponse.createResponse(401, "请先登录", response);
    }
}
