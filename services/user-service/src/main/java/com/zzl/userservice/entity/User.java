package com.zzl.userservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * 用户Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户登录账号（非用户姓名）
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

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

    /**
     * 记录创建时间，即用户注册的时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最近一次修改的时间（如修改密码、昵称等），自动更新
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除：（删除时仅仅修改标记）查询时过滤0
     */
    @TableLogic
    private Integer deleted;
}
