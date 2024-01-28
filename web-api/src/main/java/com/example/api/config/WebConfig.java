package com.example.api.config;

import com.example.api.interceptor.GatewayInterceptor;
import com.example.api.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .order(-1)
                .addPathPatterns("/**");

        registry.addInterceptor(new GatewayInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(List.of("/actuator/**", "/login/**", "/coupon/**"));

    }
}
