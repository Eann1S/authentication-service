package com.example.authentication.config.service.strategy.signing_key_generation_strategy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "secret")
public class SigningKeyGenerationStrategyConfigProperties {

    private String key;
}
