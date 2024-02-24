package com.example.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueUpdateService {

    private final QueueService queueService;

    // 매 분마다 실행되는 스케줄러
    @Scheduled(fixedRate = 5000)
    public void notifyUsersAboutQueueStatus() {
        int batchSize = 10; // 한 번에 처리할 요청의 수
        queueService.processRequestsInBatch(batchSize);
    }


}
