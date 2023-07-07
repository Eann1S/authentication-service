package com.example.authentication.dto.request;

import lombok.Builder;

@Builder
public record EmailLoginRequest(
        String email,
        String password
) {
}
