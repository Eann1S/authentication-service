package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.AccountConfirmationServiceImpl;
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
class AccountConfirmationServiceImplTests {

    @Mock
    private AccountService accountService;
    @Mock
    private ConfirmationCodeService confirmationCodeService;
    private AccountConfirmationServiceImpl accountConfirmationServiceImpl;

    @BeforeEach
    void setUp() {
        accountConfirmationServiceImpl = new AccountConfirmationServiceImpl(accountService, confirmationCodeService);
    }

    @Nested
    class SuccessCases {

        @ParameterizedTest
        @InstancioSource
        void shouldConfirmAccount_whenConfirmationCodeIsValid(Account account, String confirmationCode) {
            when(accountService.findAccountByEmailInDatabase(account.getEmail()))
                    .thenReturn(account);
            when(confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode))
                    .thenReturn(true);

            accountConfirmationServiceImpl.confirmAccountWithEmail(account.getEmail(), confirmationCode);

            verify(confirmationCodeService).invalidateConfirmationCodeOf(account);
            verify(accountService).enableAccount(account);
        }
    }

    @Nested
    class FailureCases {

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenConfirmationCodeIsInvalid(Account account, String confirmationCode) {
            when(accountService.findAccountByEmailInDatabase(account.getEmail()))
                    .thenReturn(account);
            when(confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode))
                    .thenReturn(false);

            assertThatThrownBy(() -> accountConfirmationServiceImpl.confirmAccountWithEmail(account.getEmail(), confirmationCode))
                    .hasMessage(INVALID_CONFIRMATION_CODE.getMessage());
        }
    }
}