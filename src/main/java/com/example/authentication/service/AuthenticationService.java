package com.example.authentication.service;

import com.example.authentication.dto.message.UserMessage;
import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.dto.response.JwtResponse;
import com.example.authentication.dto.response.MessageResponse;
import com.example.authentication.dto.response.UserResponse;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.exception.EntityAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.example.authentication.constant.Operation.CREATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AccountService accountService;
    private final JwtService jwtService;
    private final MessageGenerator messageGenerator;
    private final AuthenticationProvider authenticationProvider;
    private final KafkaProducerService kafkaProducerService;

    public ResponseEntity<MessageResponse> registerWithEmail(EmailRegisterRequest request) {
        if (accountService.isAccountExistsByEmail(request.email())) {
            throw new EntityAlreadyExistsException(messageGenerator.generateMessage("error.entity.already_exists", request.email()));
        }

        Account account = accountService.createAccountFromRegisterRequest(request, Role.USER, false);
        log.info("account created with email {} and id {}", account.getEmail(), account.getId());

        UserMessage userMessage = UserMessage.valueOf(new UserResponse(request.username(), request.email()), CREATE);
        kafkaProducerService.sendUserMessage(userMessage);

        return ResponseEntity.ok(
                new MessageResponse(messageGenerator.generateMessage("account.creation.success", account.getEmail()))
        );
    }

    public ResponseEntity<JwtResponse> loginWithEmail(EmailLoginRequest request) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(messageGenerator.generateMessage("error.account.invalid_credentials"));
        }

        Account account = accountService.getAccountByEmail(request.email());
        String jwt = jwtService.generateJwt(account);
        log.info("jwt for account with email {} was generated", account.getEmail());

        return ResponseEntity.ok(
                new JwtResponse(jwt)
        );
    }
}
