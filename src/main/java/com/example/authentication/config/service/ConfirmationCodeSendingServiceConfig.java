package com.example.authentication.config.service;

import com.example.authentication.service.ConfirmationCodeSendingService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.impl.ConfirmationCodeSendingServiceImpl;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConfirmationCodeSendingServiceConfig {

    @Bean
    @Qualifier("email")
    public ConfirmationCodeSendingService confirmationCodeSendingService(
            @Qualifier("email") ConfirmationCodeService confirmationCodeService,
            @Qualifier("email") ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy
    ) {
        return new ConfirmationCodeSendingServiceImpl(confirmationCodeService, confirmationCodeSendingStrategy);
    }
}
