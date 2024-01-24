package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final AccountService accountService;
    private final JwtService jwtService;

    @Override
    public void logout(String email) {
        Account account = accountService.findAccountByEmailInDatabase(email);
        jwtService.invalidateJwtOf(account);
        SecurityContextHolder.clearContext();
    }
}
