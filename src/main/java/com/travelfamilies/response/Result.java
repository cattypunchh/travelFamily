package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(CodeMessage.SUCCESS.getCode(), CodeMessage.SUCCESS.getMessage(), null);
    }

    //登陆成功时 返回token
    public static <T> Result<T> success(T data) {
        return new Result<>(CodeMessage.SUCCESS.getCode(), CodeMessage.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<>(CodeMessage.FAILED.getCode(), message, null);
    }

    public static <T> Result<T> failed(int code, String message) {

        return new Result<>(code,message,null);
    }

    /*
    * 服务器内部错误*/
    public static <T> Result<T> failed(int code, String message, T data) {

        return new Result<>(code,message,data);
    }


}
