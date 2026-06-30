package com.zzl.notifyservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_notification")
public class UserNotification {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private String redirectUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}