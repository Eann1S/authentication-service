package com.example.authentication.exception;

public class EmptyAuthenticationHeaderException extends RuntimeException {

    public EmptyAuthenticationHeaderException(String message) {
        super(message);
    }
}
