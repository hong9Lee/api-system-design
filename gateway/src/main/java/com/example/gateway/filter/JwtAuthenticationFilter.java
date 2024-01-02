package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    // 필터 설정 클래스 (여기서는 빈 클래스)
    public static class Config {
        // 필터 설정에 필요한 필드를 여기에 추가 (현재는 비워둠)
    }

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("apply info");
        return (exchange, chain) -> {
            // 요청이 들어올 때 로그 출력
            log.info("JwtAuthenticationFilter is applied to request: {}", exchange.getRequest().getPath());

            // 다음 필터 또는 라우트 핸들러로 요청 전달
            return chain.filter(exchange);
        };
    }
}
