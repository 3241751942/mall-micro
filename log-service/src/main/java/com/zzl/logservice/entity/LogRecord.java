package com.zzl.logservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("log_record")
public class LogRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String traceId;
    private String logType;
    private Long userId;
    private String username;
    private String serviceName;
    private String operation;
    private String content;
    private String requestUrl;
    private String httpMethod;
    private Integer statusCode;
    private Integer durationMs;
    private String ipAddress;
    private String userAgent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}