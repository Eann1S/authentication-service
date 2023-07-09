package com.example.authentication.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
        String username,
        String email
) {
}
