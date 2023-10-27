package com.example.authentication.controller;

import com.example.authentication.dto.MessageDto;
import com.example.authentication.service.AccountConfirmationService;
import com.example.authentication.service.ConfirmationCodeSendingService;
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
    @Qualifier("email")
    private final ConfirmationCodeSendingService confirmationCodeSendingService;

    @PostMapping("/send-code")
    public ResponseEntity<MessageDto> sendConfirmationCodeByEmail(@RequestHeader(name = "User-Id") Long id) {
        confirmationCodeSendingService.sendConfirmationCodeForAccountWithId(id);
        return ResponseEntity.ok(
                MessageDto.of(EMAIL_CONFIRMATION_CODE_SENT));
    }

    @PostMapping("/{confirmationCode}")
    public ResponseEntity<MessageDto> confirmEmail(
            @RequestHeader(name = "User-Id") Long id,
            @PathVariable String confirmationCode
    ) {
        accountConfirmationService.confirmAccountWithId(id, confirmationCode);
        return ResponseEntity.ok(
                MessageDto.of(EMAIL_CONFIRMED));
    }
}
