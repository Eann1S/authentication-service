package com.example.authentication.service;

public interface AccountConfirmationService {

    void confirmAccountWithId(Long accountId, String confirmationCode);
}
