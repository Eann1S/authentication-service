package com.example.authentication.service.impl;

import com.example.authentication.config.kafka.KafkaTopicConfig;
import com.example.authentication.dto.mq_dto.RegisterDto;
import com.example.authentication.service.UserMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.authentication.config.gson.GsonConfig.GSON;

@Service
@Qualifier("kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaJsonUserMessagingService implements UserMessagingService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicConfig kafkaTopicConfig;

    @Override
    public void sendRegisterMessage(RegisterDto registerDto) {
        String userRegisterTopic = kafkaTopicConfig.getUserRegisterTopic();
        String registerMessage = GSON.toJson(registerDto);
        kafkaTemplate.send(userRegisterTopic, registerMessage);
        log.info("{} was sent to {}", registerMessage, userRegisterTopic);
    }
}
