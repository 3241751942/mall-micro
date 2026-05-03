package com.zzl.stockservice.exception;


import com.zzl.commoncore.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StockException.class)
    public Result<Void> stockExceptionHandler(StockException e){
        return Result.error(e.getCode(),e.getMessage());
    }
}
