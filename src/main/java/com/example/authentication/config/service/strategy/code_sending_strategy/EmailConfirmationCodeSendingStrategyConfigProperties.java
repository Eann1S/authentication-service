package com.example.authentication.config.service.strategy.code_sending_strategy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.mail")
public class EmailConfirmationCodeSendingStrategyConfigProperties {

    private String username;
}
