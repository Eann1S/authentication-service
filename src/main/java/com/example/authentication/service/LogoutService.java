package com.example.authentication.service;

import com.example.authentication.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final AccountService accountService;
    private final JwtService jwtService;

    public void logout(Long accountId) {
        Account account = accountService.findAccountByIdInDatabase(accountId);
        jwtService.invalidateJwtOf(account);
        SecurityContextHolder.clearContext();
    }
}
