package com.example.authentication.service;

import com.example.authentication.config.kafka.KafkaTopicConfig;
import com.example.authentication.dto.mq_dto.RegisterDto;
import com.example.authentication.service.impl.KafkaJsonUserMessagingService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static com.example.authentication.config.gson.GsonConfig.GSON;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KafkaJsonUserMessagingServiceTests {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private KafkaTopicConfig kafkaTopicConfig;
    private KafkaJsonUserMessagingService kafkaMessagingService;

    @BeforeEach
    void setUp() {
        kafkaMessagingService = new KafkaJsonUserMessagingService(kafkaTemplate, kafkaTopicConfig);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldSendRegisterMessage(RegisterDto registerDto, String userRegisterTopic) {
        when(kafkaTopicConfig.getUserRegisterTopic())
                .thenReturn(userRegisterTopic);

        kafkaMessagingService.sendRegisterMessage(registerDto);

        verify(kafkaTemplate).send(userRegisterTopic, GSON.toJson(registerDto));
    }
}