package com.travelfamilies.controller;

import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 
     * 用户注册 
     * 
     * @param registerRequest 注册请求对象 
     * @return 操作结果 
     * @throws Exception 异常 
     */ 
    @PostMapping
    public Result<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws Exception {

        userService.registerUser(registerRequest);

        return Result.success();
    }

    /** 
     * 用户登录 
     * 
     * @param loginRequest 登录请求对象 
     * @return 登录结果 
     */ 
    @PostMapping("/login")
    public Result<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        return userService.loginUser(loginRequest);

    }

    /** 
     * 修改用户密码 
     * 
     * @param id 用户主键 ID 
     * @param updatePasswordRequest 修改密码请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/{id}/password")
    public Result<?> updateUserPassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                        HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userID");
        if (!userId.equals(id)) {

            return Result.failed("账号异常");
        } else {
            return userService.updateUserPassword(id, updatePasswordRequest);
        }

    }


    /** 
     * 修改用户详细信息 
     * 
     * @param id 用户主键 ID 
     * @param updateDetailRequest 更新用户详情请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/{id}")
    public Result<?> updateUserDetail(@PathVariable String id, @RequestBody UpdateDetailRequest updateDetailRequest,
                                      HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userID");
        long realId = Long.parseLong(id);
        if (!userId.equals(realId)) {

            return Result.failed("账号异常，请重试或者联系管理员");
        } else {
            return userService.updateUserDetail(updateDetailRequest, realId);
        }

    }

    /** 
     * 微信小程序登录 
     * 
     * @param params 包含微信 code 的参数 
     * @return 登录结果 
     */ 
    @PostMapping("/wx-login")
    public Result<?> wxLogin(@RequestBody Map<String, String> params) {

        String code = params.get("code");

        return userService.wxLogin(code);
    }

    /** 
     * 微信用户第一次登录输入基本信息
     * 
     * @param wxProfileRequest 微信资料请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/wx-profile")
    public Result<?> wxProfile(@RequestBody WxProfileRequest wxProfileRequest, HttpServletRequest httpServletRequest) {

        long userId = (Long) httpServletRequest.getAttribute("userID");
        return userService.wxProfile(wxProfileRequest, userId);
    }
}
