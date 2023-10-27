package com.example.authentication.dto;

public record JwtDto(
        String jwt
) {

    public static JwtDto of(String jwt) {
        return new JwtDto(jwt);
    }
}
