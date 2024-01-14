package com.example.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ApiAuthController {

    @GetMapping(value = "/oauth/api/validate")
    public void validate(@RequestParam("token") String token) {

        log.info("==> auth module validate logger :{}", token);
    }
}
