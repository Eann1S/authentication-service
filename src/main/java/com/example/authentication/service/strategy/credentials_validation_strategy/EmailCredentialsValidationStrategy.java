package com.example.authentication.service.strategy.credentials_validation_strategy;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.exception.EmailNotConfirmedException;
import com.example.authentication.exception.InvalidEmailCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Qualifier("email")
@RequiredArgsConstructor
public class EmailCredentialsValidationStrategy implements CredentialsValidationStrategy<EmailLoginRequest> {

    private final AuthenticationProvider authenticationProvider;

    @Override
    public void validateCredentialsFrom(EmailLoginRequest wrapperObject) {
        try {
            Authentication authentication = createAuthenticationFrom(wrapperObject);
            authenticationProvider.authenticate(authentication);
        } catch (DisabledException e) {
            throw new EmailNotConfirmedException(e);
        } catch (AuthenticationException e) {
            throw new InvalidEmailCredentialsException(e);
        }
    }

    private Authentication createAuthenticationFrom(EmailLoginRequest emailLoginRequest) {
        return new UsernamePasswordAuthenticationToken(emailLoginRequest.email(), emailLoginRequest.password());
    }
}
