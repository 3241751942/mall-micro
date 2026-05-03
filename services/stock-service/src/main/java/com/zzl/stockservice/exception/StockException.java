package com.zzl.stockservice.exception;

import lombok.Getter;

@Getter
public class StockException extends RuntimeException {
    private final Integer code;
    private final String message;

    public StockException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public StockException(String message) {
        this.code = 3020;
        this.message = message;
    }


}
