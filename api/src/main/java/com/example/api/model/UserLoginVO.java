package com.example.api.model;

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

}
