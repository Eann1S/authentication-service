package com.example.authentication.dto.mq_dto;

public record UpdateDto(
        String id,
        String email
) {
    public static UpdateDto of(String id, String email) {
        return new UpdateDto(id, email);
    }
}
