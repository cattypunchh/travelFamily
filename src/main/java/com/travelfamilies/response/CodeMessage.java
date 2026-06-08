package com.travelfamilies.response;

import lombok.Getter;

public enum CodeMessage {

    SUCCESS(200, "操作成功"),
    FAILED(404, "操作失败"),
    AUTH_FAILED(401, "未授权，请先登录"),
    ACCESS_FAILED(403, "权限不足"),
    FORBIDDEN(403, "账号异常");

    @Getter
    private final int code;
    @Getter
    private final String message;

    CodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }



}
