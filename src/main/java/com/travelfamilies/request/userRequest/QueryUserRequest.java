package com.travelfamilies.request.userRequest;

public record QueryUserRequest(
        String key,
        String value,
        String keyword,
        int requestPage,
        int requestNum
) {
}
