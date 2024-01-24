package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.exception.AccountAlreadyExistsException;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.service.impl.RegistrationServiceImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.authentication.message.ErrorMessage.ACCOUNT_ALREADY_EXISTS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class RegistrationServiceImplTests {

    @Mock
    private AccountService accountService;
    @Mock
    private UserMessagingService userMessagingService;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private ConfirmationCodeSendingService confirmationCodeSendingService;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl(accountService, userMessagingService, accountMapper, confirmationCodeSendingService);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldRegisterAccount(RegisterRequest registerRequest, Account account, RegistrationDto registrationDto) {
        when(accountService.doesAccountExistsWithEmail(registerRequest.email()))
                .thenReturn(false);
        when(accountService.createAccountFrom(eq(registerRequest), any()))
                .thenReturn(account);
        when(accountMapper.mapAccountToRegistrationDto(account, registerRequest.username()))
                .thenReturn(registrationDto);

        registrationService.register(registerRequest);

        verify(confirmationCodeSendingService).sendConfirmationCodeForAccount(account);
        verify(userMessagingService).send(registrationDto);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldThrowException_whenAccountAlreadyExists(RegisterRequest registerRequest) {
        when(accountService.doesAccountExistsWithEmail(registerRequest.email()))
                .thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(registerRequest))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessage(ACCOUNT_ALREADY_EXISTS.formatWith(registerRequest.email()));
    }
}