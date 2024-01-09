package com.example.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.api.entity.UserRegisterEntity;
import com.example.api.model.UserLoginVO;
import com.example.api.repository.UserRegisterEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwt.JwtClaims;
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
    private String SECRET_KEY = "abcdefg";
    private Long EXPIRE_MINUTE = 10L;

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

        // TODO: UserLoginVO 내부에서 JwtClaim 생성 추가 필요
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



        long expireTimeMills = System.currentTimeMillis() + (EXPIRE_MINUTE * 60 * 1000);
        builder.withExpiresAt(new Date(expireTimeMills));
        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }
}
