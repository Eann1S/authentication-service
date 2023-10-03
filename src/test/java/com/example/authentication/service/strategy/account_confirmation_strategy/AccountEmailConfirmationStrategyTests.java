package com.example.authentication.service.strategy.account_confirmation_strategy;

import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BOOLEAN;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AccountEmailConfirmationStrategyTests {

    @Mock
    private AccountRepository accountRepository;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    private AccountEmailConfirmationStrategy accountEmailConfirmationStrategy;

    @BeforeEach
    void setUp() {
        accountEmailConfirmationStrategy = new AccountEmailConfirmationStrategy(accountRepository);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldConfirmAccount(Account account) {
        accountEmailConfirmationStrategy.confirmAccount(account);

        verify(accountRepository).saveAndFlush(accountCaptor.capture());
        assertThat(accountCaptor.getValue())
                .extracting(Account::isEmailConfirmed, as(BOOLEAN))
                .isTrue();
    }
}