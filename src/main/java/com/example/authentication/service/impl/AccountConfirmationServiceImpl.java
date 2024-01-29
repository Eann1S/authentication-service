package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidConfirmationCodeException;
import com.example.authentication.service.AccountConfirmationService;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountConfirmationServiceImpl implements AccountConfirmationService {

    private final AccountService accountService;
    private final ConfirmationCodeService confirmationCodeService;

    @Override
    public void confirmAccountWithEmail(String email, String confirmationCode) {
        Account account = accountService.findAccountByEmailInDatabase(email);
        if (!confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode)) {
            throw new InvalidConfirmationCodeException();
        }
        confirmationCodeService.invalidateConfirmationCodeOf(account);
        accountService.enableAccount(account);
    }
}
