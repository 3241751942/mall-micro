package com.zzl.paymentservice.exception;

import com.zzl.commoncore.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(e.getMessage());
    }


    @ExceptionHandler(value = PaymentException.class)
    public Result<Void> handlePaymentException(Exception e) {
        return Result.error(e.getMessage());
    }
}
