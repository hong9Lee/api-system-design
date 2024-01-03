package com.example.gateway.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenVerifyResponse {
    private boolean isValid;
    private String invalidMessage;
//    private UserTokenVO userToken;
//    private Long userSeq;
    private boolean isAdmin;
    private String serviceType;
}
