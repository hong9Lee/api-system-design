package com.example.api.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class SecurityService {

    private static final String ALGORITHM = "AES";
    private final String encryptKey = "Hong!12345678900";

    public String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public String encrypt(String email) {
        if (StringUtils.isEmpty(email)) return null;

        try {
            byte[] bytes = email.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, secretKeySpec);
            byte[] encrypted = cipher.doFinal(bytes);
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return email;
        }
    }

    public String decrypt(String email) {
        if (StringUtils.isEmpty(email)) return null;

        try {
            byte[] decodeBase64 = Base64.decodeBase64(email);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, secretKeySpec);
            byte[] bytes = cipher.doFinal(decodeBase64);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
