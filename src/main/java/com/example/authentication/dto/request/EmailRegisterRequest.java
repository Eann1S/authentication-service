package com.example.authentication.dto.request;

import lombok.Builder;

@Builder
public record EmailRegisterRequest(
        String username,
        String email,
        String password
) {
}
