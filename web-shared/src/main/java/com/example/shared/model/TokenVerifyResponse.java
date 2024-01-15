package com.example.shared.model;

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
    private UserTokenVO userToken;
    private Long userSeq;
    private boolean isAdmin;
    private String serviceType;

    public TokenVerifyResponse(boolean isValid, String invalidMessage) {
        this.isValid = isValid;
        this.invalidMessage = invalidMessage;
    }
}
