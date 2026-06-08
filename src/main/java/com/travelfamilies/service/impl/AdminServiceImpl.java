package com.travelfamilies.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.config.JwtUtils;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.User;
import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.UserResponse;
import com.travelfamilies.service.AdminService;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final ImagesMapper imagesMapper;

    @Override
    public void registerAdmin(RegisterRequest registerRequest) throws BusinessException {

        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        user.setRole(registerRequest.role());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userMapper.getRegisterUser(user.getUsername()) != null) {

            throw new BusinessException("该用户名已被注册，请更换");
        }

        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        userMapper.registerUser(user, id);
        List<String> avatar = new ArrayList<>();
        avatar.add(registerRequest.avatar());
        imagesMapper.addImages(id, 5, avatar);
    }

    @Override
    public Result<?> loginAdmin(LoginRequest loginRequest) {

        String username = loginRequest.username();

        UserResponse registerUser = userMapper.getRegisterUser(username);

        if (registerUser == null) {

            return Result.failed("用户名错误");
        }

        Long userId = registerUser.getId();
        if (registerUser.getStatus() != 1) {

            stringRedisTemplate.opsForValue().set(RedisConstant.ADMIN_BLACK_LIST + userId, registerUser.getUsername() + "被封禁");
            stringRedisTemplate.delete(RedisConstant.ADMIN_TOKEN + userId);
            return Result.failed("该账号异常");
        }

        int role = registerUser.getRole();
        if (role == 1) {

            return Result.failed("此账户为用户账号，禁止登录");
        }
        if (role != loginRequest.role()) {
            String roleName = role == 2 ? "管理员" : "酒店管理员";
            return Result.failed("此为" + roleName + "账户，请选择正确的身份");
        }
        if (passwordEncoder.matches(loginRequest.password(), registerUser.getPassword())) {

            String token = jwtUtils.generateToken(userId, role, username);

            String key = role == 2 ? RedisConstant.ADMIN_TOKEN + userId : RedisConstant.HOTEL_ADMIN_TOKEN + userId;
            stringRedisTemplate.opsForValue().set(key, token,
                    RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);
            return Result.success(token);
        }
        return Result.failed("用户名或者密码不对");
    }

    @Override
    public Result<?> updateStatus(Long id) {

        return userMapper.updateStatus(id) > 0 ?
                Result.success() :
                Result.failed("状态更改失败");

    }

    @Override
    public Result<?> updatePassword(UpdatePasswordRequest updatePasswordRequest, Long id) {

        String oldPassword = userMapper.getPasswordById(id);

        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword(), oldPassword)) {

            return Result.failed("原密码错误");
        }

        String password = passwordEncoder.encode(updatePasswordRequest.newPassword());
        if (userMapper.setNewPassword(password, id) > 0) {

            String token = jwtUtils.generateToken(id, userMapper.getUserRole(id), userMapper.getUserName(id));
            int roleId= jwtUtils.getUserRoleId(token);
            String  key = roleId==2? RedisConstant.ADMIN_TOKEN+id:RedisConstant.HOTEL_ADMIN_TOKEN+id;
            stringRedisTemplate.opsForValue().set(key, token, RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);

            return Result.success(token);
        }
        return Result.failed("更新密码失败，请再次尝试");
    }

    @Override
    public Result<?> resetPass(ResetPasswordRequest resetPasswordRequest) {

        Long id = userMapper.getUserByEmail(resetPasswordRequest.email());

        if (id == null) {

            return Result.failed("邮箱输入错误");
        }

        int result = userMapper.setNewPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()), id);

        return result > 0 ? Result.success() : Result.failed("找回密码失败");

    }

    @Override
    public Result<?> getAllUser(GetDataRequest getDataRequest) {

        PageHelper.startPage(getDataRequest.requestPage(), getDataRequest.requestNum());
        List<User> users = userMapper.getAllUser();

        PageInfo<User> pageInfo = new PageInfo<>(users);
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> getUser(QueryUserRequest queryUserRequest) {

        PageHelper.startPage(queryUserRequest.requestPage(), queryUserRequest.requestNum());

        List<User> users = userMapper.queryUser(queryUserRequest);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return Result.success(pageInfo);
    }


}
