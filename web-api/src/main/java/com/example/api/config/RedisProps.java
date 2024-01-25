package com.example.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RedisProps {

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.host}")
    private String redisHost;
}
