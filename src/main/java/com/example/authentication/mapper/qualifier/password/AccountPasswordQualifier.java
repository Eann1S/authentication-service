package com.example.authentication.mapper.qualifier.password;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountPasswordQualifier {

    private final PasswordEncoder passwordEncoder;

    @EncodePassword
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
