package com.example.webwebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebWebsocketApplication.class, args);
    }

}
