package com.example.authentication.dto.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        Integer errorCode,
        String message,
        Long timestamp
) {
}
