package com.zzl.gatewayservice.filter;

import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import com.zzl.gatewayservice.config.GatewayWhitelistProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final GatewayWhitelistProperties gatewayWhitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationGlobalFilter(WebClient.Builder webClientBuilder,
                                         GatewayWhitelistProperties gatewayWhitelistProperties) {
        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();
        this.gatewayWhitelistProperties = gatewayWhitelistProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单放行
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 获取 token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("无token或格式错误：{}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 调用认证服务
        return webClient.post()
                .uri("/internal/auth/verify")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(TokenVerifyResponse.class)
                .flatMap(verifyResult -> {
                    if (!verifyResult.isValid()) {
                        log.warn("token无效：{}", path);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    // 转发用户信息
                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(verifyResult.getUserId()))
                            .header("X-User-Roles", verifyResult.getRoles())
                            .build();

                    return chain.filter(exchange.mutate().request(newRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("认证服务调用失败：{}", e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    private boolean isWhitelisted(String path) {
        if (gatewayWhitelistProperties.getWhitelist() == null) return false;
        return gatewayWhitelistProperties.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}