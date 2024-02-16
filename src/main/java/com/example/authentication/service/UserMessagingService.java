package com.example.authentication.service;

import com.example.authentication.dto.mq_dto.RegisterDto;

public interface UserMessagingService {

    void sendRegisterMessage(RegisterDto registerMessage);
}
