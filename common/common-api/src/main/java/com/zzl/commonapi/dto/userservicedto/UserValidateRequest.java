package com.zzl.commonapi.dto.userservicedto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户验证请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidateRequest {
    private String account;   // 用户名/手机号/邮箱

    private String password;
}
