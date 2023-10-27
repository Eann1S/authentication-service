package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.exception.AccountNotFoundException;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public boolean doesAccountExistsWithEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    public Account createAccountFrom(RegisterRequest registerRequest, Role role) {
        Account account = accountMapper.mapRegisterRequestToAccount(registerRequest, role);
        account = accountRepository.saveAndFlush(account);
        log.info("account {} was created", account.getId());
        return account;
    }

    public void updateAccountFrom(UpdateDto updateDto) {
        Account account = findAccountByIdInDatabase(updateDto.id());
        account = accountMapper.updateAccountFieldsFrom(updateDto, account);
        accountRepository.saveAndFlush(account);
        log.info("account {} was updated", account.getId());
    }

    public Account findAccountByIdInDatabase(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public Account findAccountByEmailInDatabase(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email));
    }
}
