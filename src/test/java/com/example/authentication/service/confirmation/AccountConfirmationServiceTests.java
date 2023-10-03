package com.example.authentication.service.confirmation;

import com.example.authentication.entity.Account;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
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
class AccountConfirmationServiceTests {

    @Mock
    private AccountService accountService;
    @Mock
    private ConfirmationCodeService confirmationCodeService;
    private AccountConfirmationService accountConfirmationService;

    @BeforeEach
    void setUp() {
        accountConfirmationService = new AccountConfirmationService(accountService, confirmationCodeService);
    }

    @Nested
    class SuccessCases {

        @ParameterizedTest
        @InstancioSource
        void shouldSendGeneratedConfirmationCode(Account account, String confirmationCode) {
            when(accountService.findAccountByIdInDatabase(account.getId()))
                    .thenReturn(account);
            when(confirmationCodeService.createConfirmationCodeFor(account))
                    .thenReturn(confirmationCode);

            accountConfirmationService.sendGeneratedConfirmationCodeForAccountWith(account.getId());

            verify(confirmationCodeService).sendConfirmationCodeFor(account, confirmationCode);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldConfirmAccount_whenConfirmationCodeIsValid(Account account, String confirmationCode) {
            when(accountService.findAccountByIdInDatabase(account.getId()))
                    .thenReturn(account);
            when(confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode))
                    .thenReturn(true);

            accountConfirmationService.confirmAccountWith(account.getId(), confirmationCode);

            verify(confirmationCodeService).invalidateConfirmationCodeOf(account);
            verify(accountService).confirmAccount(account);
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

            assertThatThrownBy(() -> accountConfirmationService.confirmAccountWith(account.getId(), confirmationCode))
                    .hasMessage(INVALID_CONFIRMATION_CODE.getMessage());
        }
    }
}