package com.example.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewaySecretProps {

    @Getter
    @Setter
    private String header;

    @Getter
    @Setter
    private String key;

}
