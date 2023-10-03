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
class JwtCacheKeyFormattingStrategyTests {

    private JwtCacheKeyFormattingStrategy jwtCacheKeyFormattingStrategy;

    @BeforeEach
    void setUp() {
        jwtCacheKeyFormattingStrategy = new JwtCacheKeyFormattingStrategy();
    }

    @ParameterizedTest
    @InstancioSource
    void formatCacheKey(Account account) {
        String cacheKey = jwtCacheKeyFormattingStrategy.formatCacheKey(account);

        assertThat(cacheKey)
                .contains(account.getId().toString(), "jwt");
    }
}