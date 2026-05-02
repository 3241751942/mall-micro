package com.zzl.productservice.Exception;

import com.zzl.commoncore.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BrandException.class)
    public Result<Void> BrandException(BrandException e){
        return Result.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(CategoryException.class)
    public Result<Void> CategoryException(CategoryException e){
        return Result.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(ProductException.class)
    public Result<Void> ProductException(ProductException e){
        return Result.error(e.getCode(),e.getMessage());
    }

}
