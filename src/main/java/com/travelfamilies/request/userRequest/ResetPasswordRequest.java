package com.travelfamilies.request.userRequest;

public record ResetPasswordRequest(
        String email,
        String newPassword
) {
}
