package com.zzl.commonapi.dto.authservicedto;

import lombok.Data;

/**
 * Token 校验响应 DTO
 */
@Data
public class TokenVerifyResponse {
    private boolean valid;
    private Long userId;
    private String username;
    private String roles;   // 可选，角色列表，逗号分隔
}