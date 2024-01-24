package com.example.authentication.message;

import lombok.Getter;

public enum InfoMessage {
    ACCOUNT_CREATED("To finish registration process, please confirm your email using confirmation code sent to you."),
    LOGGED_OUT("You've successfully logged out"),
    EMAIL_CONFIRMED("Your email was successfully confirmed!");

    @Getter
    private final String message;

    InfoMessage(String message) {
        this.message = message;
    }
}
