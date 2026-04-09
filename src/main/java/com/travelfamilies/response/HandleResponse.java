package com.travelfamilies.response;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class HandleResponse {

    public static void createResponse(int code, String message, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.failed(code, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
