package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;

public interface AccountService {

    boolean doesAccountExistsWithEmail(String email);

    Account createAccountFrom(RegisterRequest registerRequest, Role role);

    void updateAccountFrom(UpdateDto updateDto);

    Account findAccountByIdInDatabase(Long accountId);

    Account findAccountByEmailInDatabase(String email);

    void enableAccount(Account account);
}
