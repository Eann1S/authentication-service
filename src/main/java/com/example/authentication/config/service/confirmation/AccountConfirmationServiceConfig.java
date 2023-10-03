package com.example.authentication.config.service.confirmation;

import com.example.authentication.service.confirmation.AccountConfirmationService;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class AccountConfirmationServiceConfig {

    @Bean
    @Primary
    @Qualifier("email")
    public AccountConfirmationService emailAccountConfirmationService(
            @Qualifier("email") AccountService accountService,
            @Qualifier("email") ConfirmationCodeService confirmationCodeService
    ) {
        return new AccountConfirmationService(accountService, confirmationCodeService);
    }
}
