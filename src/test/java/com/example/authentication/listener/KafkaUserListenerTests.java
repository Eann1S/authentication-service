package com.example.authentication.listener;

import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.service.AccountService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static com.example.authentication.config.gson.GsonConfig.GSON;


@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KafkaUserListenerTests {

    @Mock
    private AccountService accountService;
    private KafkaUserListener kafkaUserListener;

    @BeforeEach
    void setUp() {
        kafkaUserListener = new KafkaUserListener(accountService);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUpdateAccountFromUpdateMessage(UpdateDto updateDto) {
        kafkaUserListener.updateAccountFromUpdateMessage(GSON.toJson(updateDto));

        verify(accountService).updateAccountFrom(updateDto);
    }
}