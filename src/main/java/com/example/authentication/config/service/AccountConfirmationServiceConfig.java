package com.example.authentication.config.service;

import com.example.authentication.service.AccountConfirmationService;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.impl.AccountConfirmationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AccountConfirmationServiceConfig {

    private final AccountService accountService;

    @Bean
    @Qualifier("email")
    public AccountConfirmationService accountConfirmationService(
            @Qualifier("email") ConfirmationCodeService confirmationCodeService
    ) {
        return new AccountConfirmationServiceImpl(accountService, confirmationCodeService);
    }
}
