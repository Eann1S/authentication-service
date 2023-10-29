package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.RegistrationDto;

public interface UserMessagingService {

    void send(RegistrationDto registrationDto);
}
