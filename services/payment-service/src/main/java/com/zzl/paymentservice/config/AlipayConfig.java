package com.zzl.paymentservice.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    private String appId;
    private String privateKey;
    private String alipayPublicKey;
    private String gatewayUrl;
    private String returnUrl;
    private String notifyUrl;
    private String signType = "RSA2";  // 默认 RSA2

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                gatewayUrl,
                appId,
                privateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                signType  // ✅ 修复：传 signType 而不是公钥
        );
    }
}