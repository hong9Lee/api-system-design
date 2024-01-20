package com.example.shared.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "login")
@Slf4j
public class LoginProps {

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private Long expire;

    @PostConstruct
    public void postConstruct() {
        log.info("LoginProps initialized with key: {} and expire: {}", key, expire);
    }
}


