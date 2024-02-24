package com.example.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    private final NotificationService notificationService;
    private final int maxImmediateProcess = 5; // 동시에 처리할 수 있는 최대 요청 수
    private static final String QUEUE_KEY = "requestQueue";

    public boolean isQueueEmpty() {
        return redisTemplate.opsForZSet().size(QUEUE_KEY) == 0;
    }

    public void enqueueRequest(String userId, double requestTime) {
        log.info("enqueueRequest userId:{}, requestTime:{}", userId, requestTime);
        redisTemplate.opsForZSet().add(QUEUE_KEY, userId, requestTime);
    }

    public Long getUserRank(String userId) {
        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
        log.info("getUserRank userId:{}, rank:{}", userId, rank);
        return rank;

    }

    public void removeUserFromQueue(String userId) {
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
        notificationService.notifyUser(userId, "Your turn has arrived!");
    }

    // 대기열에 있는 모든 사용자 ID를 조회
    public Set<String> getAllUserIdsInQueue() {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.range(QUEUE_KEY, 0, -1);
    }

    public boolean canProcessImmediately(Long userRank) {
        // 최대 처리 가능 수를 기반으로 바로 처리할 수 있는지 판단
        return userRank < maxImmediateProcess;
    }

    // 스케줄러에서 호출할 메소드, batchSize만큼 요청 처리
    public void processRequestsInBatch(int batchSize) {
        log.info("processRequestsInBatch init ==================");
        Set<String> userIds = redisTemplate.opsForZSet().range(QUEUE_KEY, 0, batchSize - 1);

        for (String userId : userIds) {
            log.info("processRequestsInBatch queue user:{}", userId);
        }

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        userIds.forEach(userId -> {
            removeUserFromQueue(userId);
            log.info("processRequestsInBatch remove user:{}", userId);

            // 처리 완료 알림
            notificationService.notifyUser(userId, "Your request has been processed.");
        });

        // 남은 사용자들에게 대기 순번 업데이트 알림
        updateQueuePositions();
    }

    private void updateQueuePositions() {
        getAllUserIdsInQueue().forEach(userId -> {
            Long newRank = getUserRank(userId);
            notificationService.notifyUser(userId, "Your new queue position is: " + newRank);
        });
    }

}
