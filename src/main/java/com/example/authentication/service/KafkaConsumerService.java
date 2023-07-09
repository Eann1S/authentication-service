package com.example.authentication.service;

import com.example.authentication.constant.Operation;
import com.example.authentication.dto.message.UserMessage;
import com.example.authentication.dto.response.UserResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final AccountService accountService;
    private final Gson gson;

    @Async
    @KafkaListener(topics = "${kafka.topics.authentication-service.consumer}")
    CompletableFuture<Void> receiveUserMessage(String msg) {
        log.info("received message: {}", msg);
        UserMessage userMessage = gson.fromJson(msg, UserMessage.class);
        UserResponse user = userMessage.user();

        switch (Operation.valueOf(userMessage.operation())) {
            case CREATE, UPDATE -> throw new UnsupportedOperationException("invalid operation");
            case DELETE -> accountService.deleteAccountByEmail(user.email());
        }

        return CompletableFuture.completedFuture(null);
    }
}
