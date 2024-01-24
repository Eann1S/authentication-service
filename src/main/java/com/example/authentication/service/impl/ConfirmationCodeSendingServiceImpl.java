package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.service.ConfirmationCodeSendingService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.code_sending_strategy.ConfirmationCodeSendingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationCodeSendingServiceImpl implements ConfirmationCodeSendingService {

    private final ConfirmationCodeService confirmationCodeService;
    private final ConfirmationCodeSendingStrategy confirmationCodeSendingStrategy;

    @Override
    public void sendConfirmationCodeForAccount(Account account) {
        String confirmationCode = confirmationCodeService.generateConfirmationCodeFor(account);
        confirmationCodeSendingStrategy.sendConfirmationCode(account, confirmationCode);
    }
}

