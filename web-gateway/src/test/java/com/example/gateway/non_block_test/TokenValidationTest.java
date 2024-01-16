package com.example.gateway.non_block_test;

import com.example.gateway.auth.TokenValidator;
import com.example.gateway.config.InfoProps;
import com.example.shared.model.TokenVerifyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

class TokenValidatorTest {

    private TokenValidator tokenValidator;

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        InfoProps infoProps = new InfoProps();
        infoProps.setAuth("http://localhost:8080");
        tokenValidator = new TokenValidator(webClientBuilder, infoProps);
    }

    @Test
    void validateValidTokenTest() {
        TokenVerifyResponse validResponse = new TokenVerifyResponse(true, "Valid token");
        when(responseSpec.bodyToMono(TokenVerifyResponse.class)).thenReturn(Mono.just(validResponse));

        StepVerifier.create(tokenValidator.validate("Bearer validToken"))
                .expectNextMatches(response -> response.isValid() && "Valid token".equals(response.getInvalidMessage()))
                .verifyComplete();
    }

    @Test
    void validateInvalidTokenTest() {
        TokenVerifyResponse invalidResponse = new TokenVerifyResponse(false, "Invalid token");
        when(responseSpec.bodyToMono(TokenVerifyResponse.class)).thenReturn(Mono.just(invalidResponse));

        StepVerifier.create(tokenValidator.validate("Bearer invalidToken"))
                .expectNextMatches(response -> !response.isValid() && "Invalid token".equals(response.getInvalidMessage()))
                .verifyComplete();
    }
}
