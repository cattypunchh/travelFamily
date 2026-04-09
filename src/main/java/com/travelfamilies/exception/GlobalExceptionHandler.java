package com.travelfamilies.exception;

import com.travelfamilies.response.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException businessException) {
        return Result.failed(businessException.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKeyException(DuplicateKeyException ignored) {
        return Result.failed("该用户名重复！");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {

        String messages = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.failed(messages);
    }
}
