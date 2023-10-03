package com.example.authentication.service.strategy.code_sending_strategy;

import com.example.authentication.entity.Account;

public interface ConfirmationCodeSendingStrategy {

    void sendConfirmationCode(Account account, String confirmationCode);
}
