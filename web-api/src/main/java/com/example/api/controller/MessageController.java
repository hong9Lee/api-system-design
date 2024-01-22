package com.example.api.controller;

import com.example.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/api/msg")
    public String sendMessage() {
        log.info("msg controller init");
        messageService.messageProducer();
        return "OK";
    }
}
