package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.ACCOUNT_NOT_FOUND;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Object accountProperty) {
        super(ACCOUNT_NOT_FOUND.formatWith(accountProperty));
    }
}
