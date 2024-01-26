package com.example.api.controller;

import com.example.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/api/mq/msg")
    public String sendMqMessage() {
        log.info("mq msg send controller init");
        messageService.mqMessageProducer();
        return "OK";
    }

    @GetMapping("/api/redis/msg")
    public String sendRedisMessage() {
        log.info("redis msg send controller init");
        messageService.redisMessagePublisher();
        return "OK";
    }

    @GetMapping("/api/kafka/msg")
    public String sendKafkaMessage() {
        log.info("kafka msg send controller init");
        messageService.kafkaMessageProducer();
        return "OK";
    }
}

