package com.example.gateway.filter;

import com.example.gateway.auth.TokenValidator;
import com.example.shared.config.GatewaySecretProps;
import com.example.shared.model.TokenVerifyResponse;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Resource
    private Cache<String, Mono<TokenVerifyResponse>> tokenVerifyResponseCache;
    @Resource
    private TokenValidator tokenValidator;
    private final GatewaySecretProps gatewaySecretProps;

    public static class Config {
    }

    public JwtAuthenticationFilter(GatewaySecretProps gatewaySecretProps) {
        super(Config.class);
        this.gatewaySecretProps = gatewaySecretProps;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("JwtAuthenticationFilter is applied to request: {}", exchange.getRequest().getPath());

            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = request.getHeaders();

            List<String> tokens = headers.get("X-CTM-AUTH");
            if (tokens == null || tokens.isEmpty()) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            /**
             * 메인 스레드가 비동기 요청을 보내고 서브 스레드(워커 스레드, 이벤트 루프 스레드 등)에서 요청이 처리된다.
             * 이 전체 과정은 메인 스레드를 차단하지 않는다.
             * 따라서 메인 스레드는 다른 요청을 계속 받고 처리할 수 있으며, 시스템의 전체적인 처리량과 반응성이 향상된다.
             */
            String header = tokens.get(0);
            return Mono.defer(() -> tokenVerifyResponseCache.get(header, tokenValidator::validate))
                    .flatMap(tokenVerifyResponse -> {
                        if (!tokenVerifyResponse.isValid()) {
                            log.info("Failed to validate token. header: {}, message: {}", header, tokenVerifyResponse.getInvalidMessage());
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return response.setComplete();
                        }

                        return chain.filter(exchange.mutate().request(getServerHttpRequest(exchange)).build());
                    });
        };
    }

    private ServerHttpRequest getServerHttpRequest(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .header("X-Gateway-Auth", gatewaySecretProps.getKey())
                .build();
        return serverHttpRequest;
    }
}
