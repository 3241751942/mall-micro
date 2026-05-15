package com.zzl.userservice.exception;

import com.zzl.commoncore.exception.BusinessException;
import com.zzl.commoncore.result.Result;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> businessExceptionHandler(BusinessException e){

        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 数据校验异常
     * @param e 错误实体
     * @return  Result<Void>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, msg);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public Result<Void> AddressNotFoundException(AddressNotFoundException e) {

        return Result.error(e.getCode(), e.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public Result<Void> UserNotFoundException(UserNotFoundException e) {
        return Result.error(400, e.getMessage());
    }
}
