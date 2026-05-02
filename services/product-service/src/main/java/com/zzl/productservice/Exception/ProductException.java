package com.zzl.productservice.Exception;

import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException{
    String  message;
    int code;

    public ProductException(ProductServiceBizErrorCode errorCode, String message) {
        super(message);
        this.message=message;
        this.code=errorCode.getCode();
    }

    public ProductException(ProductServiceBizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}
