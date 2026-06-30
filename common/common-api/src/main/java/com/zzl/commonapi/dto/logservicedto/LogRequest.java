package com.zzl.commonapi.dto.logservicedto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogRequest {
    private String traceId;
    @NotBlank(message="日志内容不能为空")
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
}