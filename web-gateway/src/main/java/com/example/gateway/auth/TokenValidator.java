package com.example.gateway.auth;

import com.example.gateway.config.InfoProps;
import com.example.shared.model.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final RestTemplate restTemplate;
    private final InfoProps infoProps;

    public TokenVerifyResponse validate(final String header) {

        // 기본 응답
        var res = new TokenVerifyResponse();

        if (StringUtils.isBlank(header)) {
            res.setInvalidMessage("Invalid request");
            return res;
        }

        String token = header;
        if (StringUtils.startsWith(token, "Bearer ")) {
            token = StringUtils.replaceOnce(token, "Bearer ", "");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        var entity = new HttpEntity(headers);
        try {
            // token 검증
            ResponseEntity<TokenVerifyResponse> exchange = restTemplate.exchange(infoProps.getAuth() + "/oauth/api/validate?token=" + token,
                    HttpMethod.GET, entity, TokenVerifyResponse.class);

            log.info("verify token response:{}", exchange.getBody());
            return exchange.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setValid(false);
        }
        return res;
    }
}
