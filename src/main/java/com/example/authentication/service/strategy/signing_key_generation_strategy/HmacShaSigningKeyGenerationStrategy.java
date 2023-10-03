package com.example.authentication.service.strategy.signing_key_generation_strategy;

import com.example.authentication.config.service.strategy.signing_key_generation_strategy.SigningKeyGenerationStrategyConfigProperties;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.Key;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Component
@Qualifier("hmacSha")
@RequiredArgsConstructor
public class HmacShaSigningKeyGenerationStrategy implements SigningKeyGenerationStrategy {

    private final SigningKeyGenerationStrategyConfigProperties config;

    @Override
    public Key generateSigningKey() {
        byte[] decodedKey = BASE64.decode(config.getKey());
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
