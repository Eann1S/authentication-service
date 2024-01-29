package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.exception.AccountNotFoundException;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.impl.AccountServiceImpl;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.authentication.message.ErrorMessage.ACCOUNT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTests {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository, accountMapper);
    }

    @Nested
    class SuccessCases {

        @ParameterizedTest
        @InstancioSource
        void shouldReturnTrue_whenAccountWithGivenEmailExists(Account account) {
            when(accountRepository.findByEmail(account.getEmail()))
                    .thenReturn(Optional.of(account));

            boolean accountActuallyExists = accountService.doesAccountExistsWithEmail(account.getEmail());

            assertThat(accountActuallyExists).isTrue();
        }

        @ParameterizedTest
        @InstancioSource
        void shouldCreateAccountWithUnconfirmedEmailFromRegisterRequest(Account account, RegisterRequest registerRequest) {
            when(accountMapper.mapRegisterRequestToAccount(eq(registerRequest), any()))
                    .thenReturn(account);
            when(accountRepository.saveAndFlush(account))
                    .then(returnsFirstArg());

            Account actualAccount = accountService.createAccountFrom(registerRequest, Role.USER);

            assertThat(actualAccount)
                    .extracting(Account::getEmail, Account::getPassword, Account::getAuthorities)
                    .containsExactly(account.getEmail(), account.getPassword(), List.of(Role.USER));
        }

        @ParameterizedTest
        @InstancioSource
        void shouldUpdateAccount_whenAccountExists(Account oldAccount, Account updatedAccount, UpdateDto updateDto) {
            when(accountRepository.findById(updateDto.id()))
                    .thenReturn(Optional.of(oldAccount));
            when(accountMapper.updateAccountFieldsFrom(updateDto, oldAccount))
                    .thenReturn(updatedAccount);

            accountService.updateAccountFrom(updateDto);

            verify(accountRepository).saveAndFlush(updatedAccount);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldFindAccountByIdInDatabase_whenAccountExists(Account account) {
            when(accountRepository.findById(account.getId()))
                    .thenReturn(Optional.of(account));

            Account actualAccount = accountService.findAccountByIdInDatabase(account.getId());

            assertThat(actualAccount).isEqualTo(account);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldFindAccountByEmailInDatabase_whenAccountExists(Account account) {
            when(accountRepository.findByEmail(account.getEmail()))
                    .thenReturn(Optional.of(account));

            Account actualAccount = accountService.findAccountByEmailInDatabase(account.getEmail());

            assertThat(actualAccount).isEqualTo(account);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldEnableAccount(Account account) {
            account.setEnabled(false);

            accountService.enableAccount(account);

            assertThat(account.isEnabled()).isTrue();
            verify(accountRepository).saveAndFlush(account);
        }
    }
    @Nested
    class FailureCases {

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenAccountWithGivenEmailDoesNotExist(String email) {
            assertThatThrownBy(() -> accountService.findAccountByEmailInDatabase(email))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessage(ACCOUNT_NOT_FOUND.formatWith(email));
        }

        @ParameterizedTest
        @InstancioSource
        void shouldThrowException_whenAccountWithGivenIdDoesNotExist(String id) {
            assertThatThrownBy(() -> accountService.findAccountByIdInDatabase(id))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessage(ACCOUNT_NOT_FOUND.formatWith(id));
        }

    }
}