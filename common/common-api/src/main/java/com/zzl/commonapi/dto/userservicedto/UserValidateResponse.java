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
public class UserValidateResponse {
    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private String phone;
    private String email;
    // 不返回密码哈希
}