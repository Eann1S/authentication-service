package com.example.authentication.config.service;

import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.caching.CachingService;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import com.example.authentication.service.strategy.code_generation_strategy.ConfirmationCodeGenerationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class ConfirmationCodeServiceConfig {

    private final CachingService cachingService;

    @Bean
    @Primary
    @Qualifier("email")
    public ConfirmationCodeService emailConfirmationCodeService(
            @Qualifier("email_confirmation_code") CacheKeyFormattingStrategy cacheKeyFormattingStrategy,
            @Qualifier("email") ConfirmationCodeGenerationStrategy confirmationCodeGenerationStrategy
    ) {
        return new ConfirmationCodeService(
                cachingService, cacheKeyFormattingStrategy, confirmationCodeGenerationStrategy);
    }
}
