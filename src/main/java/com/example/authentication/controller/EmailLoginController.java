package com.example.authentication.controller;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.response.JwtDto;
import com.example.authentication.service.login.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login/email")
@RequiredArgsConstructor
public class EmailLoginController {

    private final LoginService<EmailLoginRequest> loginService;

    @PostMapping
    public ResponseEntity<JwtDto> login(@Valid @RequestBody EmailLoginRequest request) {
        String jwt = loginService.login(request);
        return ResponseEntity.ok(JwtDto.of(jwt));
    }
}
