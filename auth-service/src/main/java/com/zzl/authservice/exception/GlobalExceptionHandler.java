package com.zzl.authservice.exception;

import com.zzl.commoncore.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> GlobalException(Exception e) throws Exception {
        return Result.error(e.getMessage());
    }
}
