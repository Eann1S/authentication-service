package com.example.authentication.service.messaging;

import com.example.authentication.config.kafka.KafkaTopicConfig;
import com.example.authentication.dto.mq_dto.RegistrationDto;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static com.example.authentication.json.JsonConverter.toJson;
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
    void shouldSendRegistrationDto(RegistrationDto registrationDto, String registrationTopic) {
        when(kafkaTopicConfig.getRegistrationTopic())
                .thenReturn(registrationTopic);

        kafkaMessagingService.send(registrationDto);

        verify(kafkaTemplate).send(registrationTopic, toJson(registrationDto));
    }
}