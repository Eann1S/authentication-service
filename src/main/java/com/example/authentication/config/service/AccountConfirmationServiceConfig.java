package com.example.authentication.config.service;

import com.example.authentication.service.AccountConfirmationService;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class AccountConfirmationServiceConfig {

    private final AccountService accountService;

    @Bean
    @Primary
    @Qualifier("email")
    public AccountConfirmationService emailAccountConfirmationService(
            @Qualifier("email") ConfirmationCodeService confirmationCodeService,
            @Qualifier("email") AccountConfirmationStrategy accountConfirmationStrategy
    ) {
        return new AccountConfirmationService(
                accountService, confirmationCodeService, accountConfirmationStrategy);
    }
}
