package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidAuthenticationTokenException;
import com.example.authentication.service.InternalService;
import com.example.authentication.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {

    private final JwtService jwtService;

    public String getIdOfAuthorizedAccount(String jwt) {
        Account account = jwtService.extractAccountFrom(jwt);
        if (!jwtService.isAccountAuthorized(account, jwt)) {
            throw new InvalidAuthenticationTokenException();
        }
        return account.getId();
    }
}
