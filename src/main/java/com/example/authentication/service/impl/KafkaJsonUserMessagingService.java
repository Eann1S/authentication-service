package com.example.authentication.service.impl;

import com.example.authentication.config.kafka.KafkaTopicConfig;
import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.service.UserMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.authentication.json.JsonConverter.toJson;

@Service
@Qualifier("kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaJsonUserMessagingService implements UserMessagingService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicConfig kafkaTopicConfig;

    @Override
    public void sendRegisterMessage(RegistrationDto registrationDto) {
        String registrationTopic = kafkaTopicConfig.getRegistrationTopic();
        String registerMessage = toJson(registrationDto);
        kafkaTemplate.send(registrationTopic, registerMessage);
        log.info("{} was sent to {}", registerMessage, registrationTopic);
    }
}
