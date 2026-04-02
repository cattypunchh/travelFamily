package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.pojo.User;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;

public interface UserService {
    void registerUser(RegisterRequest registerRequest) throws BusinessException;

    Result loginUser(LoginRequest loginRequest);

    Result updateUserPassword(UpdatePasswordRequest updatePasswordRequest, int userId);

    Result updateUserDetail(UpdateDetailRequest updateDetailRequest, int userId);
}
