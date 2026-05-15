package com.zzl.commonapi.feign.feignfallback.authservice;

import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import com.zzl.commonapi.feign.authservicefeign.AuthFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeignClient> {

    @Override
    public AuthFeignClient create(Throwable cause) {
        log.error("认证服务调用失败: {}", cause.getMessage(), cause);
        return authorization -> {
            TokenVerifyResponse response = new TokenVerifyResponse();
            response.setValid(false);
            return response;
        };
    }
}