package com.example.authentication.controller;

import com.example.authentication.dto.InfoMessageDto;
import com.example.authentication.service.ConfirmationCodeSendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMATION_CODE_SENT;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailConfirmationCodeSendingController {

    @Qualifier("email")
    private final ConfirmationCodeSendingService confirmationCodeSendingService;

    @PostMapping("/confirmation-code/email/send")
    public ResponseEntity<InfoMessageDto> sendConfirmationCodeByEmail(@RequestHeader(name = "User-Id") Long id) {
        confirmationCodeSendingService.sendConfirmationCodeForAccountWithId(id);
        return ResponseEntity.ok(
                InfoMessageDto.of(EMAIL_CONFIRMATION_CODE_SENT));
    }
}
