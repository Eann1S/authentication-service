package com.example.authentication.service.impl;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.LoginService;
import com.example.authentication.service.strategy.credentials_validation_strategy.CredentialsValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("email")
@RequiredArgsConstructor
public class EmailLoginService implements LoginService<EmailLoginRequest> {

    private final AccountService accountService;
    private final JwtService jwtService;
    private final CredentialsValidationStrategy<EmailLoginRequest> credentialsValidationStrategy;

    @Override
    public String login(EmailLoginRequest emailLoginRequest) {
        credentialsValidationStrategy.validateCredentialsFrom(emailLoginRequest);
        Account account = accountService.findAccountByEmailInDatabase(emailLoginRequest.email());
        jwtService.invalidateJwtOf(account);
        return jwtService.createJwtFor(account);
    }
}