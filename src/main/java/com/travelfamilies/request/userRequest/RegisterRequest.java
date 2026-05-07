package com.travelfamilies.request.userRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(


        @NotBlank(message = "用户名不能为空")
        String username,
        @NotBlank(message = "昵称不能为空")
        String nickname,
        @NotBlank(message = "请设置密码，方便后续登录")
        String password,
        @NotBlank(message = "请输入邮箱")
        String email,
        Integer role,
        @NotBlank(message = "请上传头像")
        String avatar
) {
}
