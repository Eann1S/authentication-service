package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.EXPIRED_AUTH_TOKEN;

public class ExpiredAuthenticationTokenException extends RuntimeException{

    public ExpiredAuthenticationTokenException() {
        this(null);
    }

    public ExpiredAuthenticationTokenException(Throwable cause) {
        super(EXPIRED_AUTH_TOKEN.getMessage(), cause);
    }
}
