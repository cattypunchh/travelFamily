package com.travelfamilies.service.impl;

import com.travelfamilies.config.JwtUtils;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.UserResponse;
import com.travelfamilies.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void registerUser(RegisterRequest registerRequest) throws BusinessException {

        if (userMapper.getRegisterUser(registerRequest.username()) != null) {
            throw new BusinessException("该用户名重复");
        }

        final String password = passwordEncoder.encode(registerRequest.password());

        userMapper.registerUser(registerRequest.username(),
                registerRequest.nickname(),
                password,
                registerRequest.email()
        );
    }

    @Override
    public Result<?> loginUser(LoginRequest loginRequest) {

        final UserResponse registerUser = userMapper.getRegisterUser(loginRequest.username());

        if (registerUser == null) {
            return Result.failed("该用户名不存在，请先注册");
        }

        if (passwordEncoder.matches(loginRequest.password(), registerUser.getPassword())) {
            final String token = jwtUtils.generateToken(registerUser.getId(), registerUser.getUsername());
            return Result.success(token);
        }

        return Result.failed("用户名或者密码不对");
    }

    @Override
    public Result<?> updateUserPassword(UpdatePasswordRequest updatePasswordRequest, int userId) {

        final String oldPassword = userMapper.getPasswordById(userId);

        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword(), oldPassword)) {

            return Result.failed("原密码错误，请重新输入");
        }

        final String password = passwordEncoder.encode(updatePasswordRequest.newPassword());

        return userMapper.setNewPassword(password, userId) > 0 ?
                Result.success() :
                Result.failed("更新失败，请再次重试");
    }

    @Override
    public Result<?> updateUserDetail(UpdateDetailRequest updateDetailRequest, int userId) {
        return userMapper.updateUserDetail(userId, updateDetailRequest) > 0 ?
                Result.success() :
                Result.failed("更新失败，请重新尝试");
    }
}
