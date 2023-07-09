package com.example.authentication.service;

import com.example.authentication.dto.message.UserMessage;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${kafka.topics.authentication-service.producer}")
    private String producerTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    void sendUserMessage(UserMessage userMessage) {
        String json = gson.toJson(userMessage);
        kafkaTemplate.send(producerTopic, json);
        log.info("userMessage: {} was sent to {}", json, producerTopic);
    }
}
