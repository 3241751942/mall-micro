package com.zzl.userservice.dto.response;

import com.baomidou.mybatisplus.annotation.*;
import com.zzl.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 响应用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    /**
     * 用户Id
     */
    private Long id;

    /**
     * 用户登录账号（非用户姓名）
     */
    private String username;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 头像照片url
     */
    private String avatar;

    /**
     *用户昵称（网名）
     */
    private String nickname;

    /**
     * 帐号状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 用户最后一次成功登录的时间
     */
    private LocalDateTime lastLoginTime;



}
