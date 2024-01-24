package com.example.authentication.service;

import com.example.authentication.entity.Account;

public interface ConfirmationCodeSendingService {

    void sendConfirmationCodeForAccount(Account account);
}
