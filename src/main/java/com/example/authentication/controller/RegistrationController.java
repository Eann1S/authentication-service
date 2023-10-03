package com.example.authentication.controller;

import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.dto.response.MessageDto;
import com.example.authentication.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.authentication.message.InfoMessage.ACCOUNT_CREATED;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<MessageDto> register(@Valid @RequestBody RegisterRequest request) {
        registrationService.register(request);
        return ResponseEntity.ok(
                MessageDto.of(ACCOUNT_CREATED));
    }
}
