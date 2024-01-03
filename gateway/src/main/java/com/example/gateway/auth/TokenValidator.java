package com.example.gateway.auth;

import com.example.gateway.model.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {

    public TokenVerifyResponse validate(final String header) {

        // 기본 응답
        var res = new TokenVerifyResponse();

        if(StringUtils.isBlank(header)) {
            res.setInvalidMessage("Invalid request");
            return res;
        }

        String token = header;
        if(StringUtils.startsWith(token, "Bearer ")) {
            token = StringUtils.replaceOnce(token, "Bearer ", "");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        var entity = new HttpEntity(headers);

        try {
            // auth 모듈과 통신
        } catch (Exception e) {
            log.error("");
            res.setValid(false);
        }


        return res;
    }

}
