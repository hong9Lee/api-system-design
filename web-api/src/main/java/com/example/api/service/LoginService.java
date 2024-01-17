package com.example.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.api.model.UserLoginVO;
import com.example.api.repository.UserRegisterEntityRepository;
import com.example.shared.config.LoginProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final SecurityService securityService;
    private final UserRegisterEntityRepository userRegisterEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginProps loginProps;

    public String tokenProvider(UserLoginVO userLoginVO) {
        String encryptEmail = securityService.encrypt(userLoginVO.getUserId());

        var userRegisterEntity = userRegisterEntityRepository.findByEmail(encryptEmail);
        if(!passwordEncoder.matches(userLoginVO.getUserPw(), userRegisterEntity.getUserPw())) {
            log.error("비밀번호가 일치하지 않습니다.");
            return null; // TODO: Global Exception handler 추가 필요
        }

        JWTCreator.Builder builder = JWT.create()
                .withIssuer("hong-test")
                .withAudience("test-web")
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withNotBefore(new Date(System.currentTimeMillis()));

        var jwtClaims = userLoginVO.createJwtClaims(userRegisterEntity);
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

        // TODO: props null로 전달되는 문제 해결 필요
        long expireTimeMills = System.currentTimeMillis() + (loginProps.getExpire() * 60 * 1000);
        builder.withExpiresAt(new Date(expireTimeMills));
        return builder.sign(Algorithm.HMAC256(loginProps.getKey()));
    }
}
