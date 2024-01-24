package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.ConfirmationCodeSendingServiceImpl;
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
class ConfirmationCodeSendingServiceImplTests {

    @Mock
    private ConfirmationCodeService confirmationCodeService;
    @Mock
    private ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy;
    private ConfirmationCodeSendingServiceImpl confirmationCodeSendingService;

    @BeforeEach
    void setUp() {
        confirmationCodeSendingService = new ConfirmationCodeSendingServiceImpl(confirmationCodeService, confirmationCodeSendingStrategy);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldSendConfirmationCodeForAccount(Account account, String confirmationCode) {
        when(confirmationCodeService.generateConfirmationCodeFor(account))
                .thenReturn(confirmationCode);

        confirmationCodeSendingService.sendConfirmationCodeForAccount(account);

        verify(confirmationCodeSendingStrategy).sendConfirmationCode(account, confirmationCode);
    }
}