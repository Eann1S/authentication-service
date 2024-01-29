package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.InternalServiceImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.authentication.message.ErrorMessage.INVALID_AUTH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class InternalServiceImplTests {

    @Mock
    private JwtService jwtService;
    private InternalService internalService;

    @BeforeEach
    void setUp() {
        internalService = new InternalServiceImpl(jwtService);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnIdOfAuthorizedAccount_whenAccountAuthorized(Account account, String jwt) {
        when(jwtService.extractAccountFrom(jwt))
                .thenReturn(account);
        when(jwtService.isAccountAuthorized(account, jwt))
                .thenReturn(true);

        String accountId = internalService.getIdOfAuthorizedAccount(jwt);

        assertThat(accountId).isEqualTo(account.getId());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldThrowException_whenAccountIsNotAuthorized(Account account, String jwt) {
        when(jwtService.extractAccountFrom(jwt))
                .thenReturn(account);
        when(jwtService.isAccountAuthorized(account, jwt))
                .thenReturn(false);

        assertThatThrownBy(() -> internalService.getIdOfAuthorizedAccount(jwt))
                .hasMessage(INVALID_AUTH_TOKEN.getMessage());
    }
}