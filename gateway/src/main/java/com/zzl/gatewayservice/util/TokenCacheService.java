package com.zzl.gatewayservice.util;

import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenCacheService {
    final private RedisTemplate<String,Object>  redisTemplate;
    private static final String TOKEN_CACHE_PREFIX = "auth:token:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public void cacheToken(String token, TokenVerifyResponse tokenVerifyResponse) {
        String key = TOKEN_CACHE_PREFIX + token;
        redisTemplate.opsForValue().set(key, tokenVerifyResponse,CACHE_TTL);
        log.info("成功缓存token：{}用户ID：{}", token, tokenVerifyResponse.getUserId());
    }

    public TokenVerifyResponse verifyCacheToken(String token) {
        String key = TOKEN_CACHE_PREFIX + token;
        return (TokenVerifyResponse) redisTemplate.opsForValue().get(key);
    }

    public void deleteCacheToken(String token) {
        String key = TOKEN_CACHE_PREFIX + token;
        redisTemplate.delete(key);
    }

}
