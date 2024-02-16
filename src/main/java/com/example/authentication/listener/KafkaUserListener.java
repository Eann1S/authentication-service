package com.example.authentication.listener;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.authentication.config.gson.GsonConfig.GSON;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserListener {

    private final AccountService accountService;

    @KafkaListener(topics = "#{kafkaTopicConfig.getUserUpdateTopic()}")
    public void updateAccountFromUpdateMessage(String updateMessage) {
        log.info("received update dto {}", updateMessage);
        UpdateDto updateDto = GSON.fromJson(updateMessage, UpdateDto.class);
        accountService.updateAccountFrom(updateDto);
    }
}
