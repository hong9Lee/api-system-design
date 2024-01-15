package com.example.gateway.auth;

import com.example.gateway.config.InfoProps;
import com.example.shared.model.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final WebClient.Builder webClientBuilder;
    private final InfoProps infoProps;

    public Mono<TokenVerifyResponse> validate(final String header) {
        if (StringUtils.isBlank(header)) {
            return Mono.just(new TokenVerifyResponse(false, "Invalid request"));
        }

        return webClientBuilder.build()
                .get()
                .uri(infoProps.getAuth() + "/oauth/api/validate?token=" + extractBearerToken(header))
                .retrieve()
                .bodyToMono(TokenVerifyResponse.class)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.just(new TokenVerifyResponse(false, e.getMessage()));
                });
    }

    private String extractBearerToken(String header) {
        if (StringUtils.startsWith(header, "Bearer ")) {
            return StringUtils.replaceOnce(header, "Bearer ", "");
        }
        return header;
    }
}
