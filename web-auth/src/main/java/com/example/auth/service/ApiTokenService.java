package com.example.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.example.shared.config.LoginProps;
import com.example.shared.model.TokenVerifyResponse;
import com.example.shared.model.UserTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiTokenService {
    private final LoginProps loginProps;

    public Mono<TokenVerifyResponse> verifyToken(String token) {
        return Mono.defer(() -> {
            boolean verifyToken = verifyApiToken(token);
            if (!verifyToken) return Mono.just(getInvalidTokenVO());

            try {
                var jwtConsumer = getJwtConsumer();
                var jwtClaims = jwtConsumer.processToClaims(token);
                var userTokenVO = UserTokenVO.fromJwtClaims(jwtClaims);
                return Mono.just(TokenVerifyResponse.builder()
                        .isValid(true)
                        .invalidMessage("Valid Token")
                        .userToken(userTokenVO)
                        .serviceType(userTokenVO.getServiceType())
                        .userSeq(Long.parseLong(userTokenVO.getUserSeq()))
                        .build());
            } catch (Exception e) {
                return Mono.just(TokenVerifyResponse.builder()
                        .isValid(false)
                        .invalidMessage("Invalid Token")
                        .build());
            }
        });
    }

    private boolean verifyApiToken(String token) {
        try {
            Verification verification = JWT.require(
                    Algorithm.HMAC256(loginProps.getKey()));
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
