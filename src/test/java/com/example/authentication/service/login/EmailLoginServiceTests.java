package com.example.authentication.service.login;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.strategy.credentials_validation_strategy.CredentialsValidationStrategy;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailLoginServiceTests {

    @Mock
    private AccountService accountService;
    @Mock
    private JwtService jwtService;
    @Mock
    private CredentialsValidationStrategy<EmailLoginRequest> credentialsValidationStrategy;
    private EmailLoginService emailLoginService;

    @BeforeEach
    void setUp() {
        emailLoginService = new EmailLoginService(accountService, jwtService, credentialsValidationStrategy);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldLoginAndReturnJwt(EmailLoginRequest emailLoginRequest, Account account, String jwt) {
        when(accountService.findAccountByEmailInDatabase(emailLoginRequest.email()))
                .thenReturn(account);
        when(jwtService.createJwtFor(account))
                .thenReturn(jwt);

        String actualJwt = emailLoginService.login(emailLoginRequest);

        assertThat(actualJwt).isEqualTo(jwt);
        verify(jwtService).invalidateJwtOf(account);
    }
}