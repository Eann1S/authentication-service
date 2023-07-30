package com.example.authentication.exception;

public class ExpiredAuthenticationTokenException extends RuntimeException{

    public ExpiredAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
