package com.example.authentication.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${kafka.topics.authentication-service.producer}")
    private String authenticationServiceProducerTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic(authenticationServiceProducerTopic, 1, (short) 1);
    }
}
