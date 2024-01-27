package com.example.api.service.redis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPubService {
    private final StringRedisTemplate stringRedisTemplate;

    public void sendDefaultMessage(String msg) {
        log.info("redis Received DefaultMessage :{}", msg);
        stringRedisTemplate.convertAndSend("defaultMessageTopic", msg);
    }
}
