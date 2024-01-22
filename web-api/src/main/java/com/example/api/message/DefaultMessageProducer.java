package com.example.api.message;

import com.example.shared.message.DefaultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cloud.stream.function.StreamBridge;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageProducer {

    private final StreamBridge streamBridge;

    public void notify(DefaultMessage defaultMessage) {
        boolean send = streamBridge.send("defaultMessageProducer-out-0", defaultMessage);
        log.info("DefaultMessage is sent send:{}, message:{}", send, defaultMessage);
    }
}
