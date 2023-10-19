package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.ACCOUNT_ALREADY_EXISTS;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(Object accountProperty) {
        super(ACCOUNT_ALREADY_EXISTS.formatWith(accountProperty));
    }
}
