package com.travelfamilies.request.userRequest;

import jakarta.validation.constraints.NotBlank;

public record WxProfileRequest(

        @NotBlank(message = "昵称不能为空")
        String nickname,
        String avatar
) {
}
