package com.example.authentication.service;

import com.example.authentication.entity.Account;

public interface JwtService {

    String createJwtFor(Account account);

    void invalidateJwtOf(Account account);

    Account extractAccountFrom(String jwt);

    boolean isAccountAuthorized(Account account, String jwt);
}
