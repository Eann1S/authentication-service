package com.example.authentication.service;

import com.example.authentication.dto.request.RegisterRequest;

public interface RegistrationService {

    void register(RegisterRequest registerRequest);
}
