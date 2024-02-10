package com.example.gateway.filter;

import com.example.gateway.config.CustomLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomLoadBalancerFilter extends AbstractGatewayFilterFactory<CustomLoadBalancerFilter.Config> {

    private final CustomLoadBalancer customLoadBalancer;

    // CustomLoadBalancer를 주입받습니다.
    public CustomLoadBalancerFilter(CustomLoadBalancer customLoadBalancer) {
        super(Config.class);
        this.customLoadBalancer = customLoadBalancer;
    }

    public static class Config {
        // 필터 구성을 위한 설정 클래스
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> customLoadBalancer.choose(null).flatMap(response -> {
            ServiceInstance instance = response.getServer();

            if (instance == null) {
                return Mono.error(new IllegalStateException("No instances available"));
            }

            String url = instance.getUri().toString();
            log.info("Request URL: {}", url);

            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, url);
            return chain.filter(exchange);
        });
    }
}
