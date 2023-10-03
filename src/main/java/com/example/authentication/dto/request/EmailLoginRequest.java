package com.example.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailLoginRequest(

        @NotBlank(message = "{email.not_blank}")
        String email,
        @NotBlank(message = "{password.not_blank}")
        String password
) {

        public static EmailLoginRequest of(String email, String password) {
                return new EmailLoginRequest(email, password);
        }
}
