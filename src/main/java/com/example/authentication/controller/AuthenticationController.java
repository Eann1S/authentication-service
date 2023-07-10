package com.example.authentication.controller;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.dto.response.JwtResponse;
import com.example.authentication.dto.response.MessageResponse;
import com.example.authentication.service.ActivationCodeService;
import com.example.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ActivationCodeService activationCodeService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return authenticationService.registerWithEmail(request);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody EmailLoginRequest request) {
        return authenticationService.loginWithEmail(request);
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendActivationCodeByEmail(@RequestHeader String userEmail) {
        return activationCodeService.sendActivationCodeByEmail(userEmail);
    }

    @PostMapping("/confirm/{activationCode}")
    public ResponseEntity<MessageResponse> confirmEmail(@RequestHeader String userEmail, @PathVariable String activationCode) {
        return activationCodeService.confirmEmail(userEmail, activationCode);
    }
}
