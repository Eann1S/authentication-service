package com.example.authentication.controller;

import com.example.authentication.dto.response.MessageDto;
import com.example.authentication.service.confirmation.AccountConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMATION_CODE_SENT;
import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMED;

@RestController
@RequestMapping("/api/v1/confirmation/email")
@RequiredArgsConstructor
public class EmailConfirmationController {

    @Qualifier("email")
    private final AccountConfirmationService accountConfirmationService;

    @PostMapping("/send-code")
    public ResponseEntity<MessageDto> sendConfirmationCodeByEmail(@RequestHeader(name = "User-Id") Long id) {
        accountConfirmationService.sendGeneratedConfirmationCodeForAccountWith(id);
        return ResponseEntity.ok(
                MessageDto.of(EMAIL_CONFIRMATION_CODE_SENT));
    }

    @PostMapping("/{confirmationCode}")
    public ResponseEntity<MessageDto> confirmEmail(
            @RequestHeader(name = "User-Id") Long id,
            @PathVariable String confirmationCode
    ) {
        accountConfirmationService.confirmAccountWith(id, confirmationCode);
        return ResponseEntity.ok(
                MessageDto.of(EMAIL_CONFIRMED));
    }
}
