package com.travelfamilies.request.userRequest;

public record UpdateDetailRequest(String phone, String email,
                                  String avatar, String nickname,
                                  int gender) {
}