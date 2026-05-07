package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AdminService {
    void registerAdmin(RegisterRequest registerRequest) throws BusinessException;

    Result<?> loginAdmin(LoginRequest loginRequest);

    Result<?> updateStatus(String username);

    Result<?> updatePassword(@Valid UpdatePasswordRequest updatePasswordRequest,Long id);
}
