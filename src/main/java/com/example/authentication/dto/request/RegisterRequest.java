package com.example.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.not_blank}")
        String email,
        @NotBlank(message = "{username.not_blank}")
        String username,
        @Size(message = "{password.size}", min = 8, max = 25)
        String password
) {

        public static RegisterRequest of(String email, String username, String password) {
                return new RegisterRequest(email, username, password);
        }
}
