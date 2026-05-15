package com.zzl.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String account;   // 用户名/手机/邮箱

    @NotBlank(message = "密码不能为空")
    private String password;
}