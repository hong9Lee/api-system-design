package com.example.shared.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoginProps.class)
public class LoginPropsConfig {
    @Bean
    public LoginProps loginProps() {
        return new LoginProps();
    }
}
