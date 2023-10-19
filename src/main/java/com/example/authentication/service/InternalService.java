package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidAuthenticationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalService {

    private final JwtService jwtService;

    public Long getIdOfAuthorizedAccount(String jwt) {
        Account account = jwtService.extractAccountFrom(jwt);
        if (!jwtService.isAccountAuthorized(account, jwt)) {
            throw new InvalidAuthenticationTokenException();
        }
        return account.getId();
    }
}
