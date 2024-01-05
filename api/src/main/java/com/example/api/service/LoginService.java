package com.example.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private String SECRET_KEY = "abcdefg";

    public String tokenProvider() {
        return Jwts.builder()
                .setSubject("web_test_token")
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
