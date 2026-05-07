package com.travelfamilies.service.impl;

import com.travelfamilies.config.JwtUtils;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.User;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.UserResponse;
import com.travelfamilies.service.AdminService;
import com.travelfamilies.tools.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

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
        userMapper.registerUser(user,id);

    }

    @Override
    public Result<?> loginAdmin(LoginRequest loginRequest) {

        String username = loginRequest.username();

        UserResponse registerUser = userMapper.getRegisterUser(username);

        if (registerUser == null) {

            return Result.failed("用户名错误");
        }

        Long userId = registerUser.getId();
        if (registerUser.getStatus() != 1){

            stringRedisTemplate.opsForValue().set(RedisConstant.ADMIN_BLACK_LIST+userId,registerUser.getUsername()+"被封禁");
            stringRedisTemplate.delete(RedisConstant.ADMIN_TOKEN+userId);
            return Result.failed("该账号异常");
        }


        int role = registerUser.getRole();
        if(role ==1){

            return  Result.failed("此账户为用户账号，禁止登录");
        }
        if (passwordEncoder.matches(loginRequest.password(), registerUser.getPassword())) {

            String token = jwtUtils.generateToken(userId, role, username);

            String key = role==2? RedisConstant.ADMIN_TOKEN+userId:RedisConstant.HOTEL_ADMIN_TOKEN+userId;
            stringRedisTemplate.opsForValue().set(key, token,
                                                    RedisConstant.TOKEN_EXPIRES_TIME,TimeUnit.MILLISECONDS);
            return Result.success(userId);
        }


        return Result.failed("用户名或者密码不对");
    }

    @Override
    public Result<?> updateStatus(String username) {

        return userMapper.updateStatus(username)>0 ?
                Result.success():
                Result.failed("状态更改失败");

    }

    @Override
    public Result<?> updatePassword(UpdatePasswordRequest updatePasswordRequest,Long id) {

        String oldPassword = userMapper.getPasswordById(id);

        if(!passwordEncoder.matches(updatePasswordRequest.oldPassword(), oldPassword)){

            return Result.failed("原密码错误");
        }

        String password=passwordEncoder.encode(updatePasswordRequest.newPassword());
        if(userMapper.setNewPassword(password, id)>0){

            String token = jwtUtils.generateToken(id, userMapper.getUserRole(id), userMapper.getUserName(id));
            stringRedisTemplate.opsForValue().set(RedisConstant.ADMIN_TOKEN+id,token,
                                                    RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);

            return Result.success(token);
        }
        return Result.failed("更新密码失败，请再次尝试");
    }
}
