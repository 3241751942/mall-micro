package com.zzl.commonapi.feign.authservicefeign;

import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import com.zzl.commonapi.feign.feignfallback.authservice.AuthFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", path = "/internal/auth",fallbackFactory = AuthFeignFallbackFactory.class)
public interface AuthFeignClient {

    /**
     * 校验 Token 有效性
     * @param authorization 请求头中的 Authorization 值（格式: Bearer <token>）
     * @return TokenVerifyResponse
     */
    @PostMapping("/verify")
    TokenVerifyResponse verify(@RequestHeader("Authorization") String authorization);
}