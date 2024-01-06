package com.example.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwt.JwtClaims;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class LoginService {
    private String SECRET_KEY = "abcdefg";

    public String tokenProvider() {
//        return Jwts.builder()
//                .setSubject("test_token")
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();

        Long expireMinute = 5L;
        JWTCreator.Builder builder = JWT.create()
                .withIssuer("hong-test")
                .withAudience("test-web")
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withNotBefore(new Date(System.currentTimeMillis()));

        long expireTimeMills = System.currentTimeMillis() + (expireMinute * 60 * 1000);
        builder.withExpiresAt(new Date(expireTimeMills));


        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setClaim("user", "1");
        jwtClaims.setClaim("serviceType", "test");


        if(jwtClaims != null) {
            jwtClaims.getClaimNames().forEach(claimName -> {
                try {
                    String value = jwtClaims.getStringClaimValue(claimName);
                    builder.withClaim(claimName, value);
                } catch (Exception e) {
                    log.error("jwtClaim error");
                }
            });
        }

        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }
}
