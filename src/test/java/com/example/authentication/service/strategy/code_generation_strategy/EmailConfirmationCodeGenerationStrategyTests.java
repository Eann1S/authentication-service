package com.example.authentication.service.strategy.code_generation_strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmailConfirmationCodeGenerationStrategyTests {

    private EmailConfirmationCodeGenerationStrategy emailConfirmationCodeGenerationStrategy;

    @BeforeEach
    void setUp() {
        emailConfirmationCodeGenerationStrategy = new EmailConfirmationCodeGenerationStrategy();
    }

    @Test
    void shouldGenerateConfirmationCode() {
        String confirmationCode = emailConfirmationCodeGenerationStrategy.generateConfirmationCode();

        UUID uuid = UUID.fromString(confirmationCode);
        assertThat(uuid).isNotNull();
    }
}