package com.example.worker.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "defaultTopic", groupId = "defaultGroup")
    public void listen(String message) {
        log.info("Received Kafka message in group defaultGroup:{}", message);
    }
}
