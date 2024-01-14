package com.example.auth.controller;

import com.example.auth.service.ApiTokenService;
import com.example.shared.model.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ApiAuthController {

    private final ApiTokenService apiTokenService;

    @GetMapping(value = "/oauth/api/validate")
    public TokenVerifyResponse validate(@RequestParam("token") String token) {

        log.info("==> auth module validate logger :{}", token);
        return apiTokenService.verifyToken(token);
    }
}
