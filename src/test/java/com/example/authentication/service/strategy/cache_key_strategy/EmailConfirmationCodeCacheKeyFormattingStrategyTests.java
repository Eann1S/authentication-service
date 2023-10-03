package com.example.authentication.service.strategy.cache_key_strategy;

import com.example.authentication.entity.Account;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailConfirmationCodeCacheKeyFormattingStrategyTests {

    private EmailConfirmationCodeCacheKeyFormattingStrategy emailConfirmationCodeCacheKeyFormattingStrategy;

    @BeforeEach
    void setUp() {
        emailConfirmationCodeCacheKeyFormattingStrategy = new EmailConfirmationCodeCacheKeyFormattingStrategy();
    }

    @ParameterizedTest
    @InstancioSource
    void shouldFormatCacheKeyForAccount(Account account) {
        String cacheKey = emailConfirmationCodeCacheKeyFormattingStrategy.formatCacheKey(account);

        assertThat(cacheKey)
                .contains(account.getId().toString(), "email");
    }
}