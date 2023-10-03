package com.example.authentication.dto.mq_dto;

public record RegistrationDto(
        Long id,
        String email,
        String username
) {
    public static RegistrationDto of(Long id, String email, String username) {
        return new RegistrationDto(id, email, username);
    }
}
