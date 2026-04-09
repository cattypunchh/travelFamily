package com.travelfamilies.controller;

import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //TODO    规范路径 restful风格 +  用户&管理员->redis
    @PostMapping("/register")
    public Result<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws Exception {


        userService.registerUser(registerRequest);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        return userService.loginUser(loginRequest);

    }

    @PostMapping("/update")
    public Result<?> updateUserPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest,
                                        HttpServletRequest httpServletRequest) {

        return userService.updateUserPassword(updatePasswordRequest,httpServletRequest);
    }


    @PutMapping("/updateUser")
    public Result<?> updateUserDetail(@RequestBody UpdateDetailRequest updateDetailRequest,
                                        HttpServletRequest httpServletRequest) {

        int userId = (int) httpServletRequest.getAttribute("userID");
        return userService.updateUserDetail(updateDetailRequest, userId);
    }

}
