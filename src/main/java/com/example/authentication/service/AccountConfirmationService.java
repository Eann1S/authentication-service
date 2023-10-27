package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidConfirmationCodeException;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountConfirmationService {

    private final AccountService accountService;
    private final ConfirmationCodeService confirmationCodeService;
    private final AccountConfirmationStrategy accountConfirmationStrategy;

    public void confirmAccountWithId(Long accountId, String confirmationCode) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        if (!confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode)) {
            throw new InvalidConfirmationCodeException();
    }
        confirmationCodeService.invalidateConfirmationCodeOf(account);
        accountConfirmationStrategy.confirmAccount(account);
    }
}
