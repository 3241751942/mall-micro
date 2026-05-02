package com.zzl.productservice.Exception;

import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import lombok.Getter;

@Getter
public class BrandException extends RuntimeException{
    String  message;
    int code;

    public BrandException(ProductServiceBizErrorCode errorCode, String message) {
        super(message);
        this.message=message;
        this.code=errorCode.getCode();
    }

    public BrandException(ProductServiceBizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}