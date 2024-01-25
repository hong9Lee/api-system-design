package com.example.webwebsocket.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    public void defaultMessageTopic(String message) {
        log.info("sub message :{}", message);
    }

}
