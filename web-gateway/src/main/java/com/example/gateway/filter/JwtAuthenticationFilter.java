package com.example.gateway.filter;

import com.example.gateway.auth.TokenValidator;
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
    private final String X_GATEWAY_SECRET_KEY = "967869b7-56b8-4766-8473-7baa04a499ab";
    @Resource
    private TokenValidator tokenValidator;

    public static class Config {
    }

    public JwtAuthenticationFilter() {
        super(Config.class);
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
                .header("X-Gateway-Auth", X_GATEWAY_SECRET_KEY)
                .build();
        return serverHttpRequest;
    }
}
