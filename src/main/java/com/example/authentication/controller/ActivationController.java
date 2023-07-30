package com.example.authentication.controller;

import com.example.authentication.dto.response.MessageResponse;
import com.example.authentication.service.ActivationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activation")
@RequiredArgsConstructor
public class ActivationController {

    private final ActivationCodeService activationCodeService;

    @PostMapping("/email/send-code")
    public ResponseEntity<MessageResponse> sendActivationCodeByEmail(@RequestHeader(name = "User-Email") String userEmail) {
        return activationCodeService.sendActivationCodeByEmail(userEmail);
    }

    @PostMapping("/email/{activationCode}")
    public ResponseEntity<MessageResponse> confirmEmail(
            @RequestHeader(name = "User-Email") String userEmail,
            @PathVariable String activationCode
    ) {
        return activationCodeService.confirmEmail(userEmail, activationCode);
    }
}
