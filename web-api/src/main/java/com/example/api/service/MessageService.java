package com.example.api.service;

import com.example.api.message.DefaultMessageProducer;
import com.example.shared.message.DefaultMessage;
import com.example.shared.model.MessageType;
import com.example.shared.model.event.DefaultMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final DefaultMessageProducer defaultMessageProducer;
    private final ApplicationEventPublisher applicationEventPublisher;

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


}
