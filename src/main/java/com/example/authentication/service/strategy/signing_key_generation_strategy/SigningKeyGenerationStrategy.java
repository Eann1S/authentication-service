package com.example.authentication.service.strategy.signing_key_generation_strategy;

import java.security.Key;

public interface SigningKeyGenerationStrategy {

    Key generateSigningKey();
}
