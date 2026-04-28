package com.zzl.commonapi.dto.userservicedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 对内用户信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoInternalDTO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime lastLoginTime;
    // 可包含更多内部字段，如 password 哈希（不推荐暴露，若必须则单独字段）
}
