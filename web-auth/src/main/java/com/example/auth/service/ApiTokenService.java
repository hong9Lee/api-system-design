package com.example.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.example.shared.model.TokenVerifyResponse;
import com.example.shared.model.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiTokenService {
    private String SECRET_KEY = "abcdefg";

    public TokenVerifyResponse verifyToken(String token) {
        boolean verifyToken = verifyApiToken(token);
        if (!verifyToken) return getInvalidTokenVO();

        try {
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
            return TokenVerifyResponse.builder().isValid(false).invalidMessage("Invalid Token").build();
        }
    }

    private boolean verifyApiToken(String token) {
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

    private TokenVerifyResponse getInvalidTokenVO() {
        return TokenVerifyResponse.builder()
                .isValid(false)
                .invalidMessage("Invalid Token")
                .build();
    }

    private JwtConsumer getJwtConsumer() {
        return new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                .setSkipSignatureVerification()
                .build();
    }

}
