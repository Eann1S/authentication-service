package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.caching.CachingService;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import com.example.authentication.service.strategy.code_generation_strategy.ConfirmationCodeGenerationStrategy;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationCodeServiceTests {

    @Mock
    private CachingService cachingService;
    @Mock
    private CacheKeyFormattingStrategy cacheKeyFormattingStrategy;
    @Mock
    private ConfirmationCodeGenerationStrategy confirmationCodeGenerationStrategy;
    private ConfirmationCodeService confirmationCodeService;

    @BeforeEach
    void setUp() {
        confirmationCodeService = new ConfirmationCodeService(
                cachingService, cacheKeyFormattingStrategy, confirmationCodeGenerationStrategy);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldCreateConfirmationCodeForAccount(Account account, String cacheKey, String confirmationCode) {
        when(cacheKeyFormattingStrategy.formatCacheKey(account))
                .thenReturn(cacheKey);
        when(confirmationCodeGenerationStrategy.generateConfirmationCode())
                .thenReturn(confirmationCode);

        String actualConfirmationCode = confirmationCodeService.generateConfirmationCodeFor(account);

        assertThat(actualConfirmationCode).isEqualTo(confirmationCode);
        verify(cachingService).storeInCache(eq(cacheKey), contains(confirmationCode), any());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldInvalidateConfirmationCodeOfAccount(Account account, String cacheKey) {
        when(cacheKeyFormattingStrategy.formatCacheKey(account))
                .thenReturn(cacheKey);

        confirmationCodeService.invalidateConfirmationCodeOf(account);

        verify(cachingService).deleteFromCache(cacheKey);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnTrue_whenConfirmationCodeIsValid(Account account, String cacheKey, String confirmationCode) {
        when(cacheKeyFormattingStrategy.formatCacheKey(account))
                .thenReturn(cacheKey);
        when(cachingService.getFromCache(eq(cacheKey), any()))
                .thenReturn(Optional.of(confirmationCode));

        boolean isConfirmationCodeActuallyValid = confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode);

        assertThat(isConfirmationCodeActuallyValid).isTrue();
    }
}