package com.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RouteConfig {

    @Bean
    public RouteLocator router(RouteLocatorBuilder builder) {
        return null;

        /* 아래는 java route 방식
        return builder.routes()
                .route("test-ids", r -> r.path("/api/**")
                        .uri("http://localhost:30080"))
                .build();
         */
    }
}
