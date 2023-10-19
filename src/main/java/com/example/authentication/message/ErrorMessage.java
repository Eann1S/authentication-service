package com.example.authentication.message;

import lombok.Getter;

public enum ErrorMessage {

    ACCOUNT_ALREADY_EXISTS("Account %s already exists"),
    ACCOUNT_NOT_FOUND("Account %s does not exist"),
    EXPIRED_AUTH_TOKEN("Your authentication token has expired."),
    INVALID_AUTH_TOKEN("Your authentication token is invalid."),
    INVALID_CONFIRMATION_CODE("Your confirmation code is invalid."),
    INVALID_EMAIL_CREDENTIALS("Your email or password is invalid.");

    @Getter
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String formatWith(Object property) {
        return message.formatted(property);
    }
}
