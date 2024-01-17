package com.example.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "login")
public class LoginProps {

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private Long expire;
}
