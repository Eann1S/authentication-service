package com.example.authentication.dto;

public record ErrorDto(
        String message,
        Long timestamp
) {

    public static ErrorDto of(String message, Long timestamp) {
        return new ErrorDto(message, timestamp);
    }
}
