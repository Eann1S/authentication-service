package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.EMAIL_NOT_CONFIRMED;

public class EmailNotConfirmedException extends RuntimeException {

    public EmailNotConfirmedException(Throwable cause) {
        super(EMAIL_NOT_CONFIRMED.getMessage(), cause);
    }
}
