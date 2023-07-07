package com.example.authentication.dto.response;

import lombok.Builder;

@Builder
public record JwtResponse(
        String jwt
) {
}
