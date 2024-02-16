package com.example.authentication.dto.mq_dto;

public record RegisterDto(
        String id,
        String email,
        String username
) {
}
