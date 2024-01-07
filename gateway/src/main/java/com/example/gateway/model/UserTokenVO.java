package com.example.gateway.model;

import io.micrometer.common.util.StringUtils;
import lombok.*;
import org.jose4j.jwt.JwtClaims;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenVO {
    private String userSeq;
    private String serviceType;
    private String target;

    public static UserTokenVO fromJwtClaims(JwtClaims jwtClaims) {
        String theTarget = Optional.ofNullable(jwtClaims.getClaimValue("target"))
                .map(Object::toString)
                .orElse("web");

        return UserTokenVO.builder()
                .userSeq(jwtClaims.getClaimValue("user").toString())
                .target(theTarget)
                .serviceType(jwtClaims.getClaimValue("serviceType").toString())
                .build();
    }


}
