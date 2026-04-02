package com.travelfamilies.response;

public enum CodeMessage {

    SUCCESS(200,"操作成功"),
    FAILED(404,"操作失败");


    private int code;
    private String message;

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
