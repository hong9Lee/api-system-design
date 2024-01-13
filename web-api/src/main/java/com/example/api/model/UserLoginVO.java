package com.example.api.model;

import com.example.api.entity.UserRegisterEntity;
import lombok.*;
import org.jose4j.jwt.JwtClaims;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginVO {
    private String userId;
    private String userPw;

    public JwtClaims createJwtClaims(UserRegisterEntity userRegisterEntity) {
        var jwtClaims = new JwtClaims();
        jwtClaims.setClaim("user", Long.toString(userRegisterEntity.getUserRegisterSeq()));
        jwtClaims.setClaim("serviceType", "test");
        return jwtClaims;
    }
}
