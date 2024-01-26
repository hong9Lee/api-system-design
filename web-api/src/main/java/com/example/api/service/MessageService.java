package com.example.api.service;

import com.example.api.message.DefaultMessageProducer;
import com.example.shared.message.DefaultMessage;
import com.example.shared.model.MessageType;
import com.example.shared.model.event.DefaultMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final DefaultMessageProducer defaultMessageProducer;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Transactional
    public void mqMessageProducer() {
        var randomUuid = UUID.randomUUID().toString();
        var messageType = MessageType.DEFAULT;

        var defaultMessage = DefaultMessage.builder()
                .messageType(messageType)
                .uuid(randomUuid)
                .build();

        log.info("api message producer uuid:{}, type:{}", randomUuid, messageType);
        defaultMessageProducer.notify(defaultMessage);
    }

    @Transactional
    public void redisMessagePublisher() {
        applicationEventPublisher.publishEvent(DefaultMessageEvent.builder().message("Test Event").build());
    }

    @Transactional
    public void kafkaMessageProducer() {
        String msg = "TEST KAFKA MESSAGE";
        log.info("kafkaMessageProducer send message init :{}", msg);

        CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send("defaultTopic", msg);
        send.thenAccept(result -> {
            log.info("kafka Message sent successfully to topic :{}, with offset :{}", result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
        }).exceptionally(exception -> {
            log.error("Failed to send kafka message: {}", exception.getMessage());
            return null;
        });
    }


}
