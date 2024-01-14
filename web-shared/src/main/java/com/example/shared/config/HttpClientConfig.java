package com.example.shared.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    @Bean(name = "httpClient")
    public HttpClient httpClient() {
        var socketConfig = SocketConfig.custom()
                .setSoTimeout(5, TimeUnit.SECONDS) // 소켓이 데이터를 읽거나 쓸 때 최대 대기 시간 5초 설정
                .setSoKeepAlive(false) // TCP KeepAlive 비활성화. KeepAlive는 일정 시간 동안 네트워크 활동이 없을 경우 연결이 여전히 유효한지 확인하는 기능
                .build();

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create() // HTTP 연결을 관리하기 위한 풀링 연결 관리자를 생성하기 위한 빌더
                .setDefaultSocketConfig(socketConfig)
                .setMaxConnPerRoute(30) // 특정 경로(호스트와 포트의 조합)당 최대 연결 수를 30으로 설정
                .setMaxConnTotal(100) // 연결 풀에서 관리할 수 있는 최대 연결 수를 100으로 설정
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
    }
}
