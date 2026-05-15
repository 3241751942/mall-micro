package com.zzl.gatewayservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关白名单配置属性（自定义）
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayWhitelistProperties {
    private List<String> whitelist = new ArrayList<>();
}