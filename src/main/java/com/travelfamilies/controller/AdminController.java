package com.travelfamilies.controller;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.userRequest.*;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {


    private final AdminService adminService;

    /** 
     * 管理员注册 
     * 
     * @param registerRequest 注册请求对象 
     * @return 操作结果 
     * @throws BusinessException 业务异常 
     */ 
    @PostMapping
    public Result<?> register(@Valid @RequestBody RegisterRequest registerRequest) throws BusinessException {

        adminService.registerAdmin(registerRequest);
        return Result.success();

    }

    /** 
     * 管理员登录 
     * 
     * @param loginRequest 登录请求对象 
     * @return 登录结果 
     */ 
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        return adminService.loginAdmin(loginRequest);
    }

    /** 
     * 更新用户状态
     * 
     * @param id 管理员主键 ID 
     * @return 操作结果 
     */ 
    @PutMapping("/status")
    public Result<?> updateStatus(@RequestParam String id) {

        //接口响应慢
        return adminService.updateStatus(Long.valueOf(id));
    }

    /** 
     * 修改管理员密码 
     * 
     * @param id 管理员主键 ID 
     * @param updatePasswordRequest 修改密码请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/{id}/password")
    public Result<?> updatePassword(@PathVariable Long id,
                                    @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                    HttpServletRequest httpServletRequest) {

        Long userId = (Long) httpServletRequest.getAttribute("userID");

        if (!id.equals(userId)) {

            return Result.failed("账号异常，请重试或者联系管理员");
        }
        return adminService.updatePassword(updatePasswordRequest, id);
    }

    /** 
     * 重置密码 
     * 
     * @param resetPasswordRequest 重置密码请求对象 
     * @return 操作结果 
     */ 
    @PostMapping("/resetPassword")
    public Result<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        return adminService.resetPass(resetPasswordRequest);
    }

    /** 
     * 分页查询所有用户 
     * 
     * @param getDataRequest 分页查询请求对象 
     * @return 用户列表 
     */ 
    @PostMapping("/all")
    public Result<?> getUser(@RequestBody GetDataRequest getDataRequest) {

        return adminService.getAllUser(getDataRequest);
    }

    /** 
     * 按条件查询用户 
     * 
     * @param queryUserRequest 查询用户请求对象 
     * @return 用户列表 
     */ 
    @PostMapping("/query")
    public Result<?> getUsers(@RequestBody QueryUserRequest queryUserRequest) {

        return adminService.getUser(queryUserRequest);
    }

}
