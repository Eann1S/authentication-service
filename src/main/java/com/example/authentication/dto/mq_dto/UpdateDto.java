package com.example.authentication.dto.mq_dto;

public record UpdateDto(
        Long id,
        String email
) {
    public static UpdateDto of(Long id, String email) {
        return new UpdateDto(id, email);
    }
}
