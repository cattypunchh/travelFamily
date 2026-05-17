package com.travelfamilies.config;

import com.travelfamilies.response.HandleResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException ) throws IOException{

        HandleResponse.createResponse(403, "无权限进行此操作", response);
    }
}
