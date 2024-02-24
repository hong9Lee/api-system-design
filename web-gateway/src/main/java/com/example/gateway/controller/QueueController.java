package com.example.gateway.controller;

import com.example.gateway.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/enqueue")
    public String enqueueRequest(@RequestParam String userId) {
        double requestTime = System.currentTimeMillis() / 1000.0;
        queueService.enqueueRequest(userId, requestTime);
        return "Request enqueued";
    }

    @GetMapping("/rank")
    public Long getUserRank(@RequestParam String userId) {
        return queueService.getUserRank(userId);
    }

    @DeleteMapping("/dequeue")
    public String removeUserFromQueue(@RequestParam String userId) {
        queueService.removeUserFromQueue(userId);
        return "User removed from queue";
    }




}
