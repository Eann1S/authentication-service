package com.example.authentication.controller;

import com.example.authentication.dto.InfoMessageDto;
import com.example.authentication.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.authentication.message.InfoMessage.LOGGED_OUT;

@RestController
@RequestMapping("/api/v1/logout")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping
    public ResponseEntity<InfoMessageDto> logout(@RequestHeader("User-Id") Long id) {
        logoutService.logout(id);
        return ResponseEntity.ok(
                InfoMessageDto.of(LOGGED_OUT));
    }
}
