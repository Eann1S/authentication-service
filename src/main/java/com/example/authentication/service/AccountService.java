package com.example.authentication.service;

import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final MessageGenerator messageGenerator;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageGenerator.generateMessage("error.entity.not_found", email)
                ));
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageGenerator.generateMessage("error.entity.not_found", id)
                ));
    }

    Account createAccountFromRegisterRequest(EmailRegisterRequest request, Role role, boolean isEmailConfirmed) {
        Account account = accountMapper.toEntity(request, passwordEncoder, role, isEmailConfirmed);
        return accountRepository.saveAndFlush(account);
    }

    void deleteAccountByEmail(String email) {
        accountRepository.findByEmail(email)
                .ifPresentOrElse(accountRepository::delete, () -> {
                    throw new EntityNotFoundException(
                            messageGenerator.generateMessage("error.entity.not_found", email)
                    );
                });
    }

    public boolean isAccountExistsByEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    void confirmAccountEmail(String email) {
        accountRepository.findByEmail(email)
                .map(account -> {
                    account.setEmailConfirmed(true);
                    return accountRepository.saveAndFlush(account);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        messageGenerator.generateMessage("error.entity.not_found", email)
                ));
    }
}
