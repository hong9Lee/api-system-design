package com.example.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    public void notifyUser(String userId, String message) {
        log.info("notifyUser userId:{}, message:{}", userId, message);
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                log.error("Error sending notification to userId: {}", userId, e);
                emitters.remove(userId);
                emitter.complete(); // 리소스 정리
            }
        }
    }

    @Scheduled(fixedDelay = 2500)
    public void sendHeartbeat() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
                log.info("Heartbeat sent to userId: {}", userId);
            } catch (IOException e) {
                emitters.remove(userId);
                emitter.complete();
                log.error("Error sending heartbeat to userId: {}", userId, e);
            }
        });
    }
}
