package com.example.authentication.service.strategy.credentials_validation_strategy;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.exception.InvalidEmailCredentialsException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailCredentialsValidationStrategyTests {

    @Mock
    private AuthenticationProvider authenticationProvider;
    private EmailCredentialsValidationStrategy emailCredentialsValidationStrategy;

    @BeforeEach
    void setUp() {
        emailCredentialsValidationStrategy = new EmailCredentialsValidationStrategy(authenticationProvider);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldNotThrowException_whenCredentialsAreValid(EmailLoginRequest emailLoginRequest) {
        when(authenticationProvider.authenticate(any(Authentication.class)))
                .then(returnsFirstArg());

        assertThatCode(() -> emailCredentialsValidationStrategy.validateCredentialsFrom(emailLoginRequest))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @InstancioSource
    void shouldThrowException_whenCredentialsAreInvalid(EmailLoginRequest emailLoginRequest) {
        when(authenticationProvider.authenticate(any(Authentication.class)))
                .thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> emailCredentialsValidationStrategy.validateCredentialsFrom(emailLoginRequest))
                .isInstanceOf(InvalidEmailCredentialsException.class);
    }
}