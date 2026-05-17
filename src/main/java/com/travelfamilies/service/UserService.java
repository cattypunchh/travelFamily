package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import jakarta.validation.Valid;

public interface UserService {

    void registerUser(@Valid RegisterRequest registerRequest) throws BusinessException;

    Result<?> loginUser(@Valid LoginRequest loginRequest);

    Result<?> updateUserPassword(Long userId, UpdatePasswordRequest updatePasswordRequest);

    Result<?> updateUserDetail(UpdateDetailRequest updateDetailRequest, Long userId);

    Result<?> wxLogin(String code);

    Result<?> wxProfile(WxProfileRequest wxProfileRequest, long id);
}
