package com.example.gateway.config;

import com.example.shared.model.TokenVerifyResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class TokenCacheConfig {

    @Bean
    public Cache<String, TokenVerifyResponse> tokenVerifyResponseCache() {
        // 쓰기 연산 후 30초 동안 캐시된 데이터가 사용되지 않으면 자동으로 만료
        // 캐시가 저장할 수 있는 최대 항목 수를 500으로 제한
        return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(500).build();
    }
}
