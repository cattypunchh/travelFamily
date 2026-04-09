package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface UserService {

    void registerUser(@Valid RegisterRequest registerRequest) throws BusinessException;

    Result<?> loginUser(@Valid LoginRequest loginRequest);

    Result<?> updateUserPassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest httpServletRequest);

    Result<?> updateUserDetail(UpdateDetailRequest updateDetailRequest, int userId);
}
