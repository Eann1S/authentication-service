package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.EmailAccountConfirmationService;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.authentication.message.ErrorMessage.INVALID_CONFIRMATION_CODE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailAccountConfirmationServiceTests {

    @Mock
    private AccountService accountService;
    @Mock
    private ConfirmationCodeService confirmationCodeService;
    @Mock
    private AccountConfirmationStrategy accountConfirmationStrategy;
    private EmailAccountConfirmationService emailAccountConfirmationService;

    @BeforeEach
    void setUp() {
        emailAccountConfirmationService = new EmailAccountConfirmationService(accountService, confirmationCodeService, accountConfirmationStrategy);
    }

    @Nested
    class SuccessCases {

        @ParameterizedTest
        @InstancioSource
        void shouldConfirmAccount_whenConfirmationCodeIsValid(Account account, String confirmationCode) {
            when(accountService.findAccountByIdInDatabase(account.getId()))
                    .thenReturn(account);
            when(confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode))
                    .thenReturn(true);

            emailAccountConfirmationService.confirmAccountWithId(account.getId(), confirmationCode);

            verify(confirmationCodeService).invalidateConfirmationCodeOf(account);
            verify(accountConfirmationStrategy).confirmAccount(account);
        }
    }

    @Nested
    class FailureCases {

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenConfirmationCodeIsInvalid(Account account, String confirmationCode) {
            when(accountService.findAccountByIdInDatabase(account.getId()))
                    .thenReturn(account);
            when(confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode))
                    .thenReturn(false);

            assertThatThrownBy(() -> emailAccountConfirmationService.confirmAccountWithId(account.getId(), confirmationCode))
                    .hasMessage(INVALID_CONFIRMATION_CODE.getMessage());
        }
    }
}