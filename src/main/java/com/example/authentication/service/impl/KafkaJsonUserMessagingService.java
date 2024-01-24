package com.example.authentication.service.impl;

import com.example.authentication.config.kafka.KafkaTopicConfig;
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
    public <T> void send(T message) {
        String registrationTopic = kafkaTopicConfig.getRegistrationTopic();
        String json = toJson(message);
        kafkaTemplate.send(registrationTopic, json);
        log.info("{} was sent to {}", json, registrationTopic);
    }
}
