package com.example.authentication.controller;

import com.example.authentication.dto.InfoMessageDto;
import com.example.authentication.service.AccountConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMED;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailConfirmationController {

    @Qualifier("email")
    private final AccountConfirmationService accountConfirmationService;


    @PostMapping("/confirm/email/{confirmationCode}")
    public ResponseEntity<InfoMessageDto> confirmEmail(
            @RequestHeader(name = "User-Email") String email,
            @PathVariable String confirmationCode
    ) {
        accountConfirmationService.confirmAccountWithEmail(email, confirmationCode);
        return ResponseEntity.ok(
                InfoMessageDto.of(EMAIL_CONFIRMED)
        );
    }
}
