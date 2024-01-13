package com.example.gateway.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;

import com.example.shared.model.TokenVerifyResponse;
import com.example.shared.model.UserTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {
    private String SECRET_KEY = "abcdefg";

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

//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json; charset=utf-8");
//        var entity = new HttpEntity(headers);

        try {
            // auth 모듈과 통신하는 것으로 개선 필요
            // token 검증

            boolean verifyToken = verifyToken(token);
            if (!verifyToken) return getInvalidTokenVO();

            var jwtConsumer = getJwtConsumer();
            var jwtClaims = jwtConsumer.processToClaims(token);
            var userTokenVO = UserTokenVO.fromJwtClaims(jwtClaims);
            return TokenVerifyResponse.builder()
                    .isValid(true)
                    .invalidMessage("Valid Token")
                    .userToken(userTokenVO)
                    .serviceType(userTokenVO.getServiceType())
                    .userSeq(Long.parseLong(userTokenVO.getUserSeq()))
                    .build();
        } catch (Exception e) {
            getInvalidTokenVO();
        }
        return res;
    }

    private JwtConsumer getJwtConsumer() {
        return new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                .setSkipSignatureVerification()
                .build();
    }

    private TokenVerifyResponse getInvalidTokenVO() {
        return TokenVerifyResponse.builder()
                .isValid(false)
                .invalidMessage("Invalid Token")
                .build();
    }

    // auth로 이동 필요
    private boolean verifyToken(String token) {
        try {
            Verification verification = JWT.require(
                    Algorithm.HMAC256(SECRET_KEY));
//                    .withSubject("hong-test");
            JWTVerifier jwtVerifier = verification.build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            log.error("JWT: {}", e.getMessage());
            return false;
        }
        return true;

    }

}
