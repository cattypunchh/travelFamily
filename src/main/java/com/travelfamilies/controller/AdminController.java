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

    @PostMapping
    public Result<?> register(@Valid @RequestBody RegisterRequest registerRequest) throws BusinessException {

        adminService.registerAdmin(registerRequest);
        return Result.success();

    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest loginRequest){

        return adminService.loginAdmin(loginRequest);
    }

    @PutMapping("/status")
    public Result<?> updateStatus(@RequestParam  String username){

        //接口响应慢
        return adminService.updateStatus(username);
    }

    @PutMapping("/{id}/password")
    public Result<?> updatePassword( @PathVariable Long id,
                                     @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                     HttpServletRequest httpServletRequest){

        Long userId = (Long) httpServletRequest.getAttribute("userID");

        if(!id.equals(userId)){

            return  Result.failed("账号异常，请重试或者联系管理员");
        }
        return adminService.updatePassword(updatePasswordRequest,id);
    }
}
