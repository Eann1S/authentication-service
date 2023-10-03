package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.INVALID_AUTH_TOKEN;

public class InvalidAuthenticationTokenException extends RuntimeException {

    public InvalidAuthenticationTokenException() {
        this(null);
    }

    public InvalidAuthenticationTokenException(Throwable cause) {
        super(INVALID_AUTH_TOKEN.getMessage(), cause);
    }
}
