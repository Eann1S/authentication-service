package com.example.authentication.exception;

public class InvalidAuthenticationTokenException extends RuntimeException {

    public InvalidAuthenticationTokenException(String message) {
        super(message);
    }

    public InvalidAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
