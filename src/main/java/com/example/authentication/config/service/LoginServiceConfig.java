package com.example.authentication.config.service;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.LoginService;
import com.example.authentication.service.impl.LoginServiceImpl;
import com.example.authentication.service.strategy.credentials_validation_strategy.CredentialsValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LoginServiceConfig {

    private final AccountService accountService;
    private final JwtService jwtService;

    @Bean
    @Qualifier("email")
    public LoginService<EmailLoginRequest> loginService(
            @Qualifier("email") CredentialsValidationStrategy<EmailLoginRequest> credentialsValidationStrategy
    ) {
        return new LoginServiceImpl(accountService, jwtService, credentialsValidationStrategy);
    }
}
