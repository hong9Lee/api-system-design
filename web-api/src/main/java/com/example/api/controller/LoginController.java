package com.example.api.controller;

import com.example.api.model.UserLoginVO;
import com.example.api.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public String login(@RequestBody UserLoginVO userLoginVO) {
        log.info("login controller init");
        log.info("login id:{}, pwd:{}", userLoginVO.getUserId(), userLoginVO.getUserPw());


        String loginToken = loginService.tokenProvider(userLoginVO);
        log.info("login token:{}", loginToken);

        return loginToken;
    }
}
