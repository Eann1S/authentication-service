package com.example.authentication.service.messaging;

import com.example.authentication.dto.mq_dto.RegistrationDto;

public interface UserMessagingService {

    void send(RegistrationDto registrationDto);
}
