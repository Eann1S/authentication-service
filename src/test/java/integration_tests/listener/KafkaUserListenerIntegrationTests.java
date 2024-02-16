package integration_tests.listener;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.config.kafka.KafkaTopicConfig;
import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.service.AccountService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import test_util.annotation.DisableDatabaseAutoConfiguration;
import test_util.annotation.DisableRedisAutoConfiguration;
import test_util.starter.ConfigServerStarter;
import test_util.starter.KafkaStarter;

import static com.example.authentication.config.gson.GsonConfig.GSON;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthenticationApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
@DisableRedisAutoConfiguration
@DisableDatabaseAutoConfiguration
@ExtendWith({InstancioExtension.class})
public class KafkaUserListenerIntegrationTests implements KafkaStarter, ConfigServerStarter {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private AccountService accountService;
    @Autowired
    private KafkaTopicConfig topicConfig;

    @ParameterizedTest
    @InstancioSource
    void shouldUpdateAccountFromUpdateDto_whenDtoIsSent(UpdateDto updateDto) {
        doNothing().when(accountService).updateAccountFrom(updateDto);

        sendMessage(topicConfig.getUserUpdateTopic(), GSON.toJson(updateDto));

        verify(accountService, timeout(5000)).updateAccountFrom(updateDto);
    }

    private void sendMessage(String topic, String msg) {
        kafkaTemplate.send(topic, msg);
    }
}
