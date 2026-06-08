package com.travelfamilies.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.travelfamilies.config.JwtUtils;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.User;
import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.UserResponse;
import com.travelfamilies.service.UserService;
import com.travelfamilies.tools.RedisConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final ImagesMapper imagesMapper;
    private final RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${wx.appid}")
    private String wxAppId;

    @org.springframework.beans.factory.annotation.Value("${wx.secret}")
    private String wxSecret;

    @Override
    public void registerUser(@Valid RegisterRequest registerRequest) throws BusinessException {

        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);

        if (userMapper.getRegisterUser(user.getUsername()) != null) {
            throw new BusinessException("该用户名重复");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(1);
        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        userMapper.registerUser(user, id);
    }

    @Override
    public Result<?> loginUser(@Valid LoginRequest loginRequest) {

        final UserResponse registerUser = userMapper.getRegisterUser(loginRequest.username());

        if (registerUser == null) {
            return Result.failed("该用户名不存在，请先注册");
        }
        Long userId = registerUser.getId();
        if (registerUser.getStatus() != 1) {

            stringRedisTemplate.opsForValue().set(RedisConstant.USER_BLACK_LIST + userId, registerUser.getUsername() + "被封禁");
            stringRedisTemplate.delete(RedisConstant.USER_TOKEN + userId);
            return Result.failed("该账号异常");
        }


        if (registerUser.getRole() != 1) {

            return Result.failed("非用户账号，禁止登录");
        }

        if (passwordEncoder.matches(loginRequest.password(), registerUser.getPassword())) {
            final String token = jwtUtils.generateToken(userId, registerUser.getRole(), registerUser.getUsername());

            stringRedisTemplate.opsForValue().set(RedisConstant.USER_TOKEN + userId, token,
                    RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);
            return Result.success(token);
        }

        return Result.failed("用户名或者密码不对");
    }

    @Override
    public Result<?> updateUserPassword(Long id, UpdatePasswordRequest updatePasswordRequest) {

        String oldPassword = userMapper.getPasswordById(id);

        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword(), oldPassword)) {

            return Result.failed("原密码错误，请重新输入");
        }

        String username = userMapper.getUserName(id);
        final String password = passwordEncoder.encode(updatePasswordRequest.newPassword());
        if (userMapper.setNewPassword(password, id) > 0) {

            String token = jwtUtils.generateToken(id, 1, username);

            stringRedisTemplate.opsForValue().set(RedisConstant.USER_TOKEN + id, token,
                    RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);
            return Result.success(token);
        }
        return Result.failed("更新失败，请再次重试");
    }


    @Override
    public Result<?> updateUserDetail(UpdateDetailRequest updateDetailRequest, Long userId) {

        List<String> avatar = new ArrayList<>();
        avatar.add(updateDetailRequest.avatar());
        imagesMapper.addImages(userId, 5, avatar);
        return userMapper.updateUserDetail(userId, updateDetailRequest) > 0 ?
                Result.success() :
                Result.failed("更新失败，请重新尝试");
    }

    @Override
    public Result<?> wxLogin(String code) {

        String openid = getOpenIdFromWechat(code);

        if (openid == null||openid.isEmpty()) {

            return Result.failed("微信登录失败");
        }
        User user = userMapper.getUserByOpenid(openid);

        if (user == null) {
            long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
            String password = UUID.randomUUID().toString().substring(0, 16);
            int resultLogin = userMapper.registerWxUser(
                    id, "wx_" + openid, password, "微信用户", 1, openid, null
            );
            if (!(resultLogin > 0)) {

                return Result.failed("信息保存失败");
            }

        } else if (user.getStatus() != 1) {

            return Result.failed("该账号异常");
        }
        user = userMapper.getUserByOpenid(openid);
        long userId = user.getId();
        String token = jwtUtils.generateToken(userId, 1, "wx_" + openid);
        stringRedisTemplate.opsForValue().set(RedisConstant.USER_TOKEN + userId, token,
                RedisConstant.TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        return Result.success(result);
    }

    @Override
    public Result<?> wxProfile(WxProfileRequest wxProfileRequest, long id) {

        return (userMapper.updateWxProfile(wxProfileRequest, id)) > 0
                ? Result.success()
                : Result.failed("设置信息失败");
    }

    private String getOpenIdFromWechat(String code) {

        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=" + wxAppId +
                "&secret=" + wxSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        String response = restTemplate.getForObject(url, String.class);

        if(response == null||response.isEmpty()){
            return null;
        }
        JSONObject json = JSON.parseObject(response);

        if(json==null||json.isEmpty()){
            return null;
        }
        return json.getString("openid");
    }
}
