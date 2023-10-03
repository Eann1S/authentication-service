package com.example.authentication.service.strategy.code_generation_strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Qualifier("email")
public class EmailConfirmationCodeGenerationStrategy implements ConfirmationCodeGenerationStrategy {

    @Override
    public String generateConfirmationCode() {
        return UUID.randomUUID().toString();
    }
}
