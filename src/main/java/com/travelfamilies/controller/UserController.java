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

    @PostMapping
    public Result<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws Exception {

        userService.registerUser(registerRequest);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        return userService.loginUser(loginRequest);

    }

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


    @PutMapping("/{id}")
    public Result<?> updateUserDetail(@PathVariable Long id, @RequestBody UpdateDetailRequest updateDetailRequest,
                                      HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userID");
        if (!userId.equals(id)) {

            return Result.failed("账号异常，请重试或者联系管理员");
        } else {
            return userService.updateUserDetail(updateDetailRequest, id);
        }

    }

}
