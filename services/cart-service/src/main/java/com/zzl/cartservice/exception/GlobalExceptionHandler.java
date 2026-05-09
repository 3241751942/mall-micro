package com.zzl.cartservice.exception;

import com.zzl.commoncore.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CartException.class)
    public Result<Void> cartExceptionHandler(CartException e){
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandler(Exception e){
        return Result.error(e.getMessage());
    }
}
