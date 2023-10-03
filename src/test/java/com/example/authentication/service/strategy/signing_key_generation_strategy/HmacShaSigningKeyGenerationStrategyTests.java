package com.example.authentication.service.strategy.signing_key_generation_strategy;

import com.example.authentication.config.service.strategy.signing_key_generation_strategy.SigningKeyGenerationStrategyConfigProperties;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class HmacShaSigningKeyGenerationStrategyTests {

    private static final String secretKey = "cG9ldGNvbW11bml0eXRyYW5zcG9ydGF0aW9uc29sYXJtZW5zcXVhcmVsYXJnZWJyZWE=";
    @Mock
    private SigningKeyGenerationStrategyConfigProperties configProperties;
    private HmacShaSigningKeyGenerationStrategy signingKeyGenerator;

    @BeforeEach
    void setUp() {
        signingKeyGenerator = new HmacShaSigningKeyGenerationStrategy(configProperties);
    }

    @Test
    void shouldReturnHmacShaSigningKey() {
        when(configProperties.getKey())
                .thenReturn(secretKey);

        Key signingKey = signingKeyGenerator.generateSigningKey();

        String actualSecretKey = getSecretKeyStringFrom(signingKey);
        assertThat(actualSecretKey).isEqualTo(secretKey);
    }

    private String getSecretKeyStringFrom(Key signingKey) {
        return Encoders.BASE64.encode(signingKey.getEncoded());
    }
}