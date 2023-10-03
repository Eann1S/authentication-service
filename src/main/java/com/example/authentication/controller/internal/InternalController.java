package com.example.authentication.controller.internal;

import com.example.authentication.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/account/id/{jwt}")
    public ResponseEntity<String> getIdOfAuthorizedAccount(@PathVariable String jwt) {
        Long accountId = internalService.getIdOfAuthorizedAccount(jwt);
        return ResponseEntity.ok(accountId.toString());
    }
}
