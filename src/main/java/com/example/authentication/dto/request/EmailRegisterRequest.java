package com.example.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EmailRegisterRequest(
        @NotBlank(message = "{username.not_blank}")
        String username,
        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.not_blank}")
        String email,
        @Size(message = "{password.size}", min = 8, max = 25)
        @NotBlank(message = "{password.not_blank}")
        String password
) {
}
