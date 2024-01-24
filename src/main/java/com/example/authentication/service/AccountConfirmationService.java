package com.example.authentication.service;

public interface AccountConfirmationService {

    void confirmAccountWithEmail(String email, String confirmationCode);
}
