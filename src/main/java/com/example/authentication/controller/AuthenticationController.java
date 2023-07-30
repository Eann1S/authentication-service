package com.example.authentication.controller;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.dto.response.JwtResponse;
import com.example.authentication.dto.response.MessageResponse;
import com.example.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/email/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return authenticationService.registerWithEmail(request);
    }

    @PostMapping("/email/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody EmailLoginRequest request) {
        return authenticationService.loginWithEmail(request);
    }
}
