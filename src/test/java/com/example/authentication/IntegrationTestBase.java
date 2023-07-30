package com.example.authentication;

import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.service.EmailService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;

import static com.example.authentication.constant.GlobalConstants.*;
import static org.apache.commons.codec.CharEncoding.UTF_8;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@Import({IntegrationTestBase.TestKafkaConsumerService.class, IntegrationTestBase.TestAppConfig.class})
@Transactional
@Testcontainers(parallel = true)
@ActiveProfiles("test")
@DirtiesContext
@SuppressWarnings("all")
public class IntegrationTestBase {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-rc-alpine3.18"))
            .withExposedPorts(6379);
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine3.17"))
            .withExposedPorts(5432);
    @Container
    private static final GenericContainer<?> configServer = new GenericContainer<>(DockerImageName.parse("eann1s/cloud-config-server:1.0.0"))
            .withExposedPorts(8888)
            .waitingFor(Wait.forHttp("/authentication-service/test").forStatusCode(200));
    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"));

    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    protected EmailService emailService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    static void setEnvironmentVariables() {
        System.setProperty("CONFIG_SERVER_URL", configServer.getHost() + ":" + configServer.getFirstMappedPort());
    }

    @TestComponent
    @Slf4j
    @Getter
    protected static class TestKafkaConsumerService {

        private final CountDownLatch latch = new CountDownLatch(1);
        private String messagePayload;

        @KafkaListener(topics = "${kafka.topics.authentication-service.producer}")
        public void receive(String message) {
            log.info("received message: {}", message);
            this.messagePayload = message;
            this.latch.countDown();
        }
    }

    @TestConfiguration
    static class TestAppConfig {
        @Bean
        public MessageSource testMessageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:validationMessages-test");
            messageSource.setDefaultEncoding(UTF_8);
            return messageSource;
        }
    }

    protected void registerTestAccount(EmailRegisterRequest request) {
        authenticationService.registerWithEmail(request);
    }

    protected void registerTestAccountWithDefaults() {
        authenticationService.registerWithEmail(new EmailRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD));
    }

    protected void performRequest(
            MockMvc mockMvc,
            MockHttpServletRequestBuilder requestBuilder,
            ResultMatcher... resultMatcher
    ) throws Exception {
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        expectResult(resultActions, resultMatcher);
    }

    protected void expectResult(ResultActions resultActions, ResultMatcher... resultMatcher) throws Exception {
        resultActions.andExpectAll(resultMatcher);
    }
}


