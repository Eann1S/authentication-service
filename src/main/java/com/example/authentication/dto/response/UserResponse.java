package com.example.authentication.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
        String username,
        String email
) {

    public static UserResponse valueOf(String username, String email) {
        return UserResponse.builder()
                .username(username)
                .email(email)
                .build();
    }
}
