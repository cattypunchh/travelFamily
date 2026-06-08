package com.travelfamilies.exception;

import com.travelfamilies.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException businessException) {
        log.warn("业务异常: {}", businessException.getMessage());
        return Result.failed(businessException.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKeyException(DuplicateKeyException e) {
        log.warn("数据重复: {}", e.getMessage());
        return Result.failed("该信息重复！");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {

        String messages = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", messages);
        return Result.failed(messages);
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<?> nullPointerExceptionHandler(NullPointerException e) {
        log.error("空指针异常: {}", e.getMessage(), e);
        return Result.failed(500, "系统内部错误：数据处理异常", null);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception exception) {
        log.error("服务器内部错误", exception);
        return Result.failed(500, "服务器内部错误", "请联系管理员");
    }

}
