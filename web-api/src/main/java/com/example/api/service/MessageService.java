package com.example.api.service;

import com.example.api.message.DefaultMessageProducer;
import com.example.shared.message.DefaultMessage;
import com.example.shared.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final DefaultMessageProducer defaultMessageProducer;

    public void messageProducer() {
        var randomUuid = UUID.randomUUID().toString();
        var messageType = MessageType.DEFAULT;

        var defaultMessage = DefaultMessage.builder()
                .messageType(messageType)
                .uuid(randomUuid)
                .build();

        log.info("api message producer uuid:{}, type:{}", randomUuid, messageType);
        defaultMessageProducer.notify(defaultMessage);
    }


}
