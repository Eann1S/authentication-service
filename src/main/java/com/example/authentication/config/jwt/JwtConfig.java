package com.example.authentication.config.jwt;

import com.example.authentication.service.strategy.signing_key_generation_strategy.SigningKeyGenerationStrategy;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final SigningKeyGenerationStrategy signingKeyGenerationStrategy;

    @Bean
    public JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(signingKeyGenerationStrategy.generateSigningKey());
    }

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parserBuilder().setSigningKey(signingKeyGenerationStrategy.generateSigningKey()).build();
    }
}
