package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidConfirmationCodeException;
import com.example.authentication.service.AccountConfirmationService;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("email")
@RequiredArgsConstructor
public class EmailAccountConfirmationService implements AccountConfirmationService {

    private final AccountService accountService;
    @Qualifier("email")
    private final ConfirmationCodeService confirmationCodeService;
    @Qualifier("email")
    private final AccountConfirmationStrategy accountConfirmationStrategy;

    @Override
    public void confirmAccountWithId(Long accountId, String confirmationCode) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        if (!confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode)) {
            throw new InvalidConfirmationCodeException();
    }
        confirmationCodeService.invalidateConfirmationCodeOf(account);
        accountConfirmationStrategy.confirmAccount(account);
    }
}
