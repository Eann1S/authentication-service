package com.example.authentication.dto.mq_dto;

public record RegistrationDto(
        String id,
        String email,
        String username
) {
    public static RegistrationDto of(String id, String email, String username) {
        return new RegistrationDto(id, email, username);
    }
}
