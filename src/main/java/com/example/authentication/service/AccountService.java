package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final MessageGenerator messageGenerator;

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageGenerator.generateMessage("error.entity.not_found", email)
                ));
    }
}
