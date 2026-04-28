package com.zzl.userservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *  修改密码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    /**
     * 原来的密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
