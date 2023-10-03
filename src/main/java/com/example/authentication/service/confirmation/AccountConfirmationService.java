package com.example.authentication.service.confirmation;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidConfirmationCodeException;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountConfirmationService {

    private final AccountService accountService;
    private final ConfirmationCodeService confirmationCodeService;

    public void sendGeneratedConfirmationCodeForAccountWith(Long accountId) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        String confirmationCode = confirmationCodeService.createConfirmationCodeFor(account);
        confirmationCodeService.sendConfirmationCodeFor(account, confirmationCode);
    }

    public void confirmAccountWith(Long accountId, String confirmationCode) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        if (!confirmationCodeService.isConfirmationCodeOfAccountValid(account, confirmationCode)) {
            throw new InvalidConfirmationCodeException();
        }
        confirmationCodeService.invalidateConfirmationCodeOf(account);
        accountService.confirmAccount(account);
    }
}
