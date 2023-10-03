package com.example.authentication.service.strategy.credentials_validation_strategy;

public interface CredentialsValidationStrategy<T> {

    void validateCredentialsFrom(T wrapperObject);
}
