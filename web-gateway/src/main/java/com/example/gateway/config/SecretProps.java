package com.example.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "secret")
public class SecretProps {

    @Getter
    @Setter
    private String header;

    @Getter
    @Setter
    private String key;

}
