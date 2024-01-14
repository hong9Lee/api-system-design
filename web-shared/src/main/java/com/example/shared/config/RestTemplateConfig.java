package com.example.shared.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final HttpClient httpClient;

    @Bean
    public RestTemplate restTemplate() {
        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(5 * 1000);
        return new RestTemplate(factory);
    }
}
