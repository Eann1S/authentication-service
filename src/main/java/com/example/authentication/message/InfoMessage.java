package com.example.authentication.message;

import lombok.Getter;

public enum InfoMessage {
    ACCOUNT_CREATED("Your account was successfully created!"),
    LOGGED_OUT("You've successfully logged out"),
    EMAIL_CONFIRMATION_CODE_SENT("To confirm your email, please enter the confirmation code sent to you."),
    EMAIL_CONFIRMED("Your email was successfully confirmed!");

    @Getter
    private final String message;

    InfoMessage(String message) {
        this.message = message;
    }
}
