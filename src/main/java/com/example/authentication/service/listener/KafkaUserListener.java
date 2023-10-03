package com.example.authentication.service.listener;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.authentication.json.JsonConverter.fromJson;


@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserListener {

    private final AccountService accountService;

    @KafkaListener(topics = "#{kafkaTopicConfig.getUserUpdateTopic()}")
    public void updateAccountFromUpdateDtoMessage(String updateDtoMessage) {
        log.info("received update dto {}", updateDtoMessage);
        UpdateDto updateDto = fromJson(updateDtoMessage, UpdateDto.class);
        accountService.updateAccountFrom(updateDto);
    }
}
