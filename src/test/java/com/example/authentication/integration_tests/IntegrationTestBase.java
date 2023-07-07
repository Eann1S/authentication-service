package com.example.authentication.integration_tests;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@Import(IntegrationTestBase.TestKafkaConsumer.class)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Transactional
@Testcontainers(parallel = true)
@ActiveProfiles("test")
@SuppressWarnings("all")
public class IntegrationTestBase {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-rc-alpine3.18"))
            .withExposedPorts(6379);
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine3.17"))
            .withExposedPorts(5432);


    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    }

    @TestComponent
    @Slf4j
    @Getter
    static class TestKafkaConsumer {

        private CountDownLatch latch = new CountDownLatch(1);
        private String messagePayload;

        @KafkaListener(topics = "${kafka.topics.authentication-service}")
        public void receive(String message) {
            log.info("received message: {}", message);
            messagePayload = message;
            latch.countDown();
        }
    }
}


