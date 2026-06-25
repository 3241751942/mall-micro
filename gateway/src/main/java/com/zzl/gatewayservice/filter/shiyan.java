package com.zzl.gatewayservice.filter;

/**
import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class shiyan implements GlobalFilter, Ordered {
    private WebClient webClient;
    private String token;
    public shiyan(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/").build();
    }

    public Mono<Void> filer(ServerWebExchange exchange, GatewayFilterChain chain){


        return webClient.post()
                .uri("/internal/auth/verify")
                .header(HttpHeaders.AUTHORIZATION,token)
                .retrieve()
                .bodyToMono(TokenVerifyResponse.class)
                .flatMap(a->{
                    if(!a.isValid()){
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                        return exchange.getResponse().setComplete();
                    }
                    ServerHttpRequest newRequest=exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(a.getUserId()))
                            .header("Role","admin")
                            .build();

                    return chain.filter(exchange.mutate().request(newRequest).build());
                }).onErrorResume(e->{
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
 */

