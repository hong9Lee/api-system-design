package com.example.gateway.filter;

import com.example.gateway.auth.TokenValidator;
import com.example.gateway.model.TokenVerifyResponse;
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

import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Resource
    private Cache<String, TokenVerifyResponse> tokenVerifyResponseCache;

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
            var tokenVerifyResponse = tokenVerifyResponseCache.get(header,
                    tokenValidator::validate);
            if (!tokenVerifyResponse.isValid()) {
                log.info("failed to validate token. header: {}, message: {}", header, tokenVerifyResponse.getInvalidMessage());
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            return chain.filter(exchange);
        };
    }
}
