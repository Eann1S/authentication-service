package com.example.authentication.config.service;

import com.example.authentication.service.AccountService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.caching.CachingService;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class JwtServiceConfig {

    private final AccountService accountService;
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;
    private final CachingService cachingService;

    @Bean
    @Primary
    public JwtService jwtService(
            @Qualifier("jwt") CacheKeyFormattingStrategy cacheKeyFormattingStrategy
    ) {
        return new JwtService(accountService, jwtBuilder, jwtParser, cachingService, cacheKeyFormattingStrategy);
    }
}
