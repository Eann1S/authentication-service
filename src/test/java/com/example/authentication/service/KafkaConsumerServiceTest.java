package com.example.authentication.service;

import com.example.authentication.dto.message.UserMessage;
import com.example.authentication.dto.response.UserResponse;
import com.example.authentication.IntegrationTestBase;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.example.authentication.constant.Operation.DELETE;
import static com.example.authentication.constant.GlobalConstants.TEST_EMAIL;
import static com.example.authentication.constant.GlobalConstants.TEST_USERNAME;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@SuppressWarnings("all")
public class KafkaConsumerServiceTest extends IntegrationTestBase {

    @Value("${kafka.topics.authentication-service.consumer}")
    private String consumerTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;
    private final KafkaConsumerService kafkaConsumerService;
    @MockBean
    private final AccountService accountService;

    @Test
    public void consumerTest() {
        doNothing()
                .when(accountService).deleteAccountByEmail(anyString());

        UserMessage userMessage = UserMessage.valueOf(new UserResponse(TEST_USERNAME, TEST_EMAIL), DELETE);
        String message = gson.toJson(userMessage);
        CompletableFuture<Void> future = kafkaConsumerService.receiveUserMessage(message);

        sendMessage(consumerTopic, message);
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(future::isDone);

        verify(accountService, times(1)).deleteAccountByEmail(TEST_EMAIL);
    }

    private void sendMessage(String topic, String msg) {
        kafkaTemplate.send(topic, msg);
    }
}
