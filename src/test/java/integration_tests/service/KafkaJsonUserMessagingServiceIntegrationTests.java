package integration_tests.service;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.service.UserMessagingService;
import org.apache.commons.lang.StringUtils;
import org.awaitility.Awaitility;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.ActiveProfiles;
import test_util.annotation.DisableDatabaseAutoConfiguration;
import test_util.annotation.DisableRedisAutoConfiguration;
import test_util.starter.ConfigServerStarter;
import test_util.starter.KafkaStarter;

import java.util.concurrent.TimeUnit;

import static com.example.authentication.json.JsonConverter.fromJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AuthenticationApplication.class, KafkaJsonUserMessagingServiceIntegrationTests.TestKafkaListener.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@DisableDatabaseAutoConfiguration
@DisableRedisAutoConfiguration
public class KafkaJsonUserMessagingServiceIntegrationTests implements KafkaStarter, ConfigServerStarter {

    @Autowired
    @Qualifier("kafka")
    private UserMessagingService userMessagingService;
    @Autowired
    private TestKafkaListener testKafkaListener;

    @ParameterizedTest
    @InstancioSource
    void shouldSendDto(RegistrationDto registrationDto) {
        userMessagingService.send(registrationDto);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> testKafkaListener.isMessageReceived());

        RegistrationDto actualRegistrationDto = fromJson(testKafkaListener.messagePayload, RegistrationDto.class);
        assertThat(actualRegistrationDto).isEqualTo(registrationDto);
    }

    @TestComponent
    static class TestKafkaListener {
        private String messagePayload;

        @KafkaListener(topics = "#{kafkaTopicConfig.getRegistrationTopic()}")
        void receiveUserUpdateMessage(String message) {
            messagePayload = message;
        }

        private boolean isMessageReceived() {
            return StringUtils.isNotBlank(messagePayload);
        }
    }
}
