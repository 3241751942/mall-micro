package com.zzl.commoncore.enums;


import lombok.Getter;

@Getter
public enum BizErrorCode {
    // 系统级错误（1xxx）
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试"),

    // 参数错误（2xxx）
    INVALID_PARAM(2001, "参数无效"),
    MISSING_PARAM(2002, "缺少必要参数"),

    // 业务错误（3xxx）
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_ALREADY_EXISTS(3002, "用户已存在"),
    PASSWORD_ERROR(3003, "密码错误"),
    ORDER_NOT_FOUND(3004, "订单不存在"),
    ADDRESS_NOT_FOUND(3005,"未找到地址"),

    // 权限错误（4xxx）
    FORBIDDEN(4001, "无权限操作"),
    TOKEN_EXPIRED(4002, "Token已过期");

    private final Integer code;
    private final String message;

    BizErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
