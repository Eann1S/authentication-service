package com.example.authentication.service;

public interface ConfirmationCodeSendingService {

    void sendConfirmationCodeForAccountWithId(Long accountId);
}
