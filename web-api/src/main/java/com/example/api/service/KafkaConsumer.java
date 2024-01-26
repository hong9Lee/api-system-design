package com.example.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "defaultTopic", groupId = "defaultGroup")
    public void listen(String message) {
        log.info("Received message in group defaultGroup:{}", message);
    }
}
