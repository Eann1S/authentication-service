package com.example.authentication.producer;

import com.example.authentication.dto.message.Message;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    @Value("${kafka.topics.authentication-service}")
    private String authenticationServiceTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    public <T> void send(Message<T> message, Type type) {
        String json = gson.toJson(message, type);
        kafkaTemplate.send(authenticationServiceTopic, json);
        log.info("message: {} was sent to {}", json, authenticationServiceTopic);
    }
}
