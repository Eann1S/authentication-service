package com.example.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailLoginRequest(
        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.not_blank}")
        String email,
        @NotBlank(message = "{password.not_blank}")
        String password
) {
}
