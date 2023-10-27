package com.example.authentication.config.service;

import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeSendingService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class ConfirmationCodeSendingServiceConfig {

    private final AccountService accountService;

    @Bean
    @Primary
    @Qualifier("email")
    public ConfirmationCodeSendingService emailConfirmationCodeSendingService(
            @Qualifier("email") ConfirmationCodeService confirmationCodeService,
            @Qualifier("email") ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy
    ) {
        return new ConfirmationCodeSendingService(accountService, confirmationCodeService, confirmationCodeSendingStrategy);
    }
}
