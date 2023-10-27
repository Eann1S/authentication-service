package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmationCodeSendingService {

    private final AccountService accountService;
    private final ConfirmationCodeService confirmationCodeService;
    private final ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy;

    public void sendConfirmationCodeForAccountWithId(Long accountId) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        String confirmationCode = confirmationCodeService.generateConfirmationCodeFor(account);
        confirmationCodeSendingStrategy.sendConfirmationCode(account, confirmationCode);
    }
}
