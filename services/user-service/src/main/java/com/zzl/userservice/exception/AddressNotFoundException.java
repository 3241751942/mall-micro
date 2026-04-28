package com.zzl.userservice.exception;


import com.zzl.userservice.enums.UserServiceBizErrorCode;
import lombok.Getter;


@Getter
public class AddressNotFoundException extends RuntimeException{
    private final String message;
    private final Integer  code;

    /**
     * 使用枚举
     * @param errorCode 枚举的错误码和信息
     */
    public AddressNotFoundException(UserServiceBizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     *   枚举 + 自定义消息
     */
    public AddressNotFoundException(UserServiceBizErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }
}
