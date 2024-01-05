package com.example.api.controller;

import com.example.api.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String login() {
        log.info("login controller init");


        String loginToken = loginService.tokenProvider();
        log.info("login token:{}", loginToken);

        return loginToken;
    }
}
