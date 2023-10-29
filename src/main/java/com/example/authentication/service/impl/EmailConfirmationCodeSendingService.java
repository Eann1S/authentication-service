package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeSendingService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("email")
@RequiredArgsConstructor
public class EmailConfirmationCodeSendingService implements ConfirmationCodeSendingService {

    private final AccountService accountService;
    @Qualifier("email")
    private final ConfirmationCodeService confirmationCodeService;
    @Qualifier("email")
    private final ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy;

    @Override
    public void sendConfirmationCodeForAccountWithId(Long accountId) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        String confirmationCode = confirmationCodeService.generateConfirmationCodeFor(account);
        confirmationCodeSendingStrategy.sendConfirmationCode(account, confirmationCode);
    }
}

