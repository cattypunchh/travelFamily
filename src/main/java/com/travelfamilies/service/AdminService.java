package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import jakarta.validation.Valid;

public interface AdminService {
    void registerAdmin(RegisterRequest registerRequest) throws BusinessException;

    Result<?> loginAdmin(LoginRequest loginRequest);

    Result<?> updateStatus(Long id);

    Result<?> updatePassword(@Valid UpdatePasswordRequest updatePasswordRequest, Long id);

    Result<?> resetPass(ResetPasswordRequest resetPasswordRequest);

    Result<?> getAllUser(GetDataRequest getDataRequest);

    Result<?> getUser(QueryUserRequest queryUserRequest);

}
