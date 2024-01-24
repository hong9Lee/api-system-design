package com.example.api.service.listener;

import com.example.api.service.redis.RedisPubService;
import com.example.shared.model.event.DefaultMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageListener {
    private final RedisPubService redisPubService;

    @Async
    @TransactionalEventListener
    public void sendDefaultMessageToRedisListener(DefaultMessageEvent defaultMessageEvent) {
        log.info("sendDefaultMessageToRedisListener event:{}", defaultMessageEvent);
        redisPubService.sendDefaultMessage(defaultMessageEvent.getMessage());
    }
}
