package com.zzl.gatewayservice.filter;

import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import com.zzl.commonapi.feign.authservicefeign.AuthFeignClient;
import com.zzl.gatewayservice.config.GatewayWhitelistProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final AuthFeignClient authFeignClient;
    private final GatewayWhitelistProperties gatewayWhitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Autowired
    public JwtAuthenticationGlobalFilter(@Lazy AuthFeignClient authFeignClient,
                                         GatewayWhitelistProperties gatewayWhitelistProperties) {
        this.authFeignClient = authFeignClient;
        this.gatewayWhitelistProperties = gatewayWhitelistProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少 Authorization 头或格式错误，路径: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        TokenVerifyResponse verifyResult;
        try {
            verifyResult = authFeignClient.verify(authHeader);
        } catch (Exception e) {
            log.error("调用 auth-service 校验 Token 失败: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }

        if (!verifyResult.isValid()) {
            log.warn("Token 无效，路径: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(verifyResult.getUserId()))
                .header("X-User-Roles", verifyResult.getRoles())
                .build();

        log.debug("认证通过，userId: {}, path: {}", verifyResult.getUserId(), path);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isWhitelisted(String path) {
        if (gatewayWhitelistProperties.getWhitelist() == null) {
            return false;
        }
        return gatewayWhitelistProperties.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}