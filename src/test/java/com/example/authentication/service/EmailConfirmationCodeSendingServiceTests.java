package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.EmailConfirmationCodeSendingService;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailConfirmationCodeSendingServiceTests {

    @Mock
    private AccountService accountService;
    @Mock
    private ConfirmationCodeService confirmationCodeService;
    @Mock
    private ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy;
    private EmailConfirmationCodeSendingService confirmationCodeSendingService;

    @BeforeEach
    void setUp() {
        confirmationCodeSendingService = new EmailConfirmationCodeSendingService(
                accountService, confirmationCodeService, confirmationCodeSendingStrategy);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldSendConfirmationCodeForAccountWithId(Account account, String confirmationCode) {
        when(accountService.findAccountByIdInDatabase(account.getId()))
                .thenReturn(account);
        when(confirmationCodeService.generateConfirmationCodeFor(account))
                .thenReturn(confirmationCode);

        confirmationCodeSendingService.sendConfirmationCodeForAccountWithId(account.getId());

        verify(confirmationCodeSendingStrategy).sendConfirmationCode(account, confirmationCode);
    }
}