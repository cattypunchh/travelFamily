package com.travelfamilies.response;

public enum CodeMessage {

    SUCCESS(200,"操作成功"),
    FAILED(404,"操作失败"),
    AUTH_TAILED(401,"未授权，请先登录"),
    ACCESS_FAILED(403,"权限不足");


    private final int code;
    private final String message;

    CodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
