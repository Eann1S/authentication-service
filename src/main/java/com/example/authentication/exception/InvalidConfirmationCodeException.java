package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.INVALID_CONFIRMATION_CODE;

public class InvalidConfirmationCodeException extends RuntimeException {

    public InvalidConfirmationCodeException() {
        super(INVALID_CONFIRMATION_CODE.getMessage());
    }
}
