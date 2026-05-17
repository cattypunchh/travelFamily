package com.travelfamilies.request.userRequest;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(

        @NotBlank(message = "旧密码不能为空")
        String oldPassword,

        @NotBlank(message = "新密码不能为空")
        String newPassword) {
}
