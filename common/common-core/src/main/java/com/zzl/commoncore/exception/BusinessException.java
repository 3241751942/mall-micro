package com.zzl.commoncore.exception;

import com.zzl.commoncore.enums.BizErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String message;

    /**
     * 使用枚举
     * @param errorCode 枚举的错误码和信息
     */
    public BusinessException(BizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     *   枚举 + 自定义消息
     */
    public BusinessException(BizErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

}
