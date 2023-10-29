package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.ExpiredAuthenticationTokenException;
import com.example.authentication.service.impl.JwtServiceImpl;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJws;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.authentication.message.ErrorMessage.EXPIRED_AUTH_TOKEN;
import static com.example.authentication.message.ErrorMessage.INVALID_AUTH_TOKEN;
import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.jwsHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class JwtServiceImplTests {

    @Mock
    private AccountService accountService;
    @Mock
    private CachingService cachingService;
    @Mock(answer = Answers.RETURNS_SELF)
    private JwtBuilder jwtBuilder;
    @Mock
    private JwtParser jwtParser;
    @Mock
    private CacheKeyFormattingStrategy cacheKeyFormattingStrategy;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(accountService, jwtBuilder, jwtParser, cachingService, cacheKeyFormattingStrategy);
    }

    @Nested
    class SuccessCases {
        @ParameterizedTest
        @InstancioSource
        void shouldCreateJwtForAccount(Account account, String cacheKey) {
            when(cacheKeyFormattingStrategy.formatCacheKey(account))
                    .thenReturn(cacheKey);
            when(jwtBuilder.compact())
                    .thenReturn("jwt");

            String jwt = jwtService.createJwtFor(account);

            verify(cachingService).storeInCache(eq(cacheKey), contains(jwt), any());
        }

        @ParameterizedTest
        @InstancioSource
        void shouldInvalidateJwtOfAccount(Account account, String cacheKey) {
            when(cacheKeyFormattingStrategy.formatCacheKey(account))
                    .thenReturn(cacheKey);

            jwtService.invalidateJwtOf(account);

            verify(cachingService).deleteFromCache(cacheKey);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldExtractAccountFromJwt(Account account, String jwt) {
            when(jwtParser.parseClaimsJws(jwt))
                    .thenReturn(createJwsWith(account));
            when(accountService.findAccountByEmailInDatabase(account.getEmail()))
                    .thenReturn(account);

            Account actualAccount = jwtService.extractAccountFrom(jwt);

            assertThat(actualAccount).isEqualTo(account);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldReturnTrue_whenAccountAuthorized(Account account, String cacheKey, String jwt) {
            when(cacheKeyFormattingStrategy.formatCacheKey(account))
                    .thenReturn(cacheKey);
            when(cachingService.getFromCache(cacheKey, String.class))
                    .thenReturn(Optional.of(jwt));

            boolean isAccountAuthorized = jwtService.isAccountAuthorized(account, jwt);

            assertThat(isAccountAuthorized).isTrue();
        }
    }

    @Nested
    class FailureCases {

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenJwtIsInvalid(String jwt) {
            when(jwtParser.parseClaimsJws(jwt))
                    .thenThrow(MalformedJwtException.class);

            assertThatThrownBy(() -> jwtService.extractAccountFrom(jwt))
                    .hasMessage(INVALID_AUTH_TOKEN.getMessage());
        }

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenJwtHasExpired(String jwt) {
            when(jwtParser.parseClaimsJws(jwt))
                    .thenThrow(ExpiredJwtException.class);

            assertThatThrownBy(() -> jwtService.extractAccountFrom(jwt))
                    .isInstanceOf(ExpiredAuthenticationTokenException.class)
                    .hasMessage(EXPIRED_AUTH_TOKEN.getMessage());
        }

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenJwtHasExpired(Account account, String cacheKey, String jwt) {
            when(cacheKeyFormattingStrategy.formatCacheKey(account))
                    .thenReturn(cacheKey);

            assertThatThrownBy(() -> jwtService.isAccountAuthorized(account, jwt))
                    .isInstanceOf(ExpiredAuthenticationTokenException.class)
                    .hasMessage(EXPIRED_AUTH_TOKEN.getMessage());
        }

        @ParameterizedTest
        @InstancioSource
        void shouldReturnFalse_whenJwtIsInvalid(Account account, String cacheKey, String jwt, String invalidJwt) {
            when(cacheKeyFormattingStrategy.formatCacheKey(account))
                    .thenReturn(cacheKey);
            when(cachingService.getFromCache(cacheKey, String.class))
                    .thenReturn(Optional.of(jwt));

            boolean isAccountAuthorized = jwtService.isAccountAuthorized(account, invalidJwt);

            assertThat(isAccountAuthorized).isFalse();
        }
    }

    private Jws<Claims> createJwsWith(Account account) {
        Claims claims = createClaimsWith(account);
        return new DefaultJws<>(jwsHeader(), claims, "");
    }

    private Claims createClaimsWith(Account account) {
        return claims().setSubject(account.getEmail());
    }
}