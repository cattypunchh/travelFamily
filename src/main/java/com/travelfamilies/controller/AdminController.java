package com.travelfamilies.controller;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.userRequest.LoginRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.request.userRequest.UpdatePasswordRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest registerRequest) throws BusinessException {

        adminService.registerAdmin(registerRequest);
        return Result.success();

    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest loginRequest){

        return adminService.loginAdmin(loginRequest);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateStatus")
    public Result<?> updateStatus(@RequestParam  String username, HttpServletRequest httpServletRequest){

        //接口响应慢
        return adminService.updateStatus(username);
    }

    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,HttpServletRequest httpServletRequest){

        return adminService.updatePassword(updatePasswordRequest,httpServletRequest);
    }
}
