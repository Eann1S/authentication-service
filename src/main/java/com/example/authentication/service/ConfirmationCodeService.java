package com.example.authentication.service;

import com.example.authentication.entity.Account;

public interface ConfirmationCodeService {

    String generateConfirmationCodeFor(Account account);

    void invalidateConfirmationCodeOf(Account account);

    boolean isConfirmationCodeOfAccountValid(Account account, String confirmationCode);
}
