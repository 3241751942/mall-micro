package com.zzl.userservice.enums;

import lombok.Getter;

@Getter
public enum UserServiceBizErrorCode {
    // 系统级错误（1xxx）
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试"),

    // 参数错误（2xxx）
    INVALID_PARAM(2001, "参数无效"),
    MISSING_PARAM(2002, "缺少必要参数"),

    // 业务错误（3xxx）
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_ALREADY_EXISTS(3002, "用户已存在"),
    ADDRESS_NOT_FOUND(3003,"该地址不存在");

    // 权限错误（4xxx）

    private final Integer code;
    private final String message;

    UserServiceBizErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
