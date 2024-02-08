package com.example.webeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WebEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebEurekaApplication.class, args);
    }
}
