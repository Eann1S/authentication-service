package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.exception.AccountNotFoundException;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.strategy.account_confirmation_strategy.AccountConfirmationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountConfirmationStrategy accountConfirmationStrategy;

    public void confirmAccount(Account account) {
        accountConfirmationStrategy.confirmAccount(account);
    }

    public boolean accountExistsWithEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    public Account createAccountFrom(RegisterRequest registerRequest, Role role) {
        Account account = accountMapper.mapRegisterRequestToAccount(registerRequest, role, false);
        encodeAndSetPasswordTo(account, registerRequest.password());
        Account savedAccount = accountRepository.saveAndFlush(account);
        log.info("account {} was created", account.getId());
        return savedAccount;
    }

    public void updateAccountFrom(UpdateDto updateDto) {
        Account account = findAccountByIdInDatabase(updateDto.id());
        Account updatedAccount = accountMapper.updateAccountFieldsFrom(updateDto, account);
        accountRepository.saveAndFlush(updatedAccount);
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

    private void encodeAndSetPasswordTo(Account account, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        account.setPassword(encodedPassword);
    }
}
