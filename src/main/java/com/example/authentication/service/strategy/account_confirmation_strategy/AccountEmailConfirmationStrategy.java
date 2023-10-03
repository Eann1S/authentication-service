package com.example.authentication.service.strategy.account_confirmation_strategy;

import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("email")
@RequiredArgsConstructor
@Slf4j
public class AccountEmailConfirmationStrategy implements AccountConfirmationStrategy {

    private final AccountRepository accountRepository;

    @Override
    public void confirmAccount(Account account) {
        account.setEmailConfirmed(true);
        accountRepository.saveAndFlush(account);
        log.info("email {} was confirmed", account.getEmail());
    }
}
