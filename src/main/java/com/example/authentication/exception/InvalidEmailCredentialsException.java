package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.INVALID_EMAIL_CREDENTIALS;

public class InvalidEmailCredentialsException extends RuntimeException {

    public InvalidEmailCredentialsException() {
        super(INVALID_EMAIL_CREDENTIALS.getMessage());
    }
}
