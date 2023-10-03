package com.example.authentication.config.service;

import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import com.example.authentication.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AccountServiceConfig {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Primary
    @Qualifier("email")
    public AccountService emailAccountService(
            @Qualifier("email") AccountConfirmationStrategy accountConfirmationStrategy
    ) {
        return new AccountService(accountRepository, accountMapper, passwordEncoder, accountConfirmationStrategy);
    }
}
