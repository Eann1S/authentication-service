package com.example.authentication.controller.internal;

import com.example.authentication.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/authorized-account/id/{jwt}")
    public ResponseEntity<String> getIdOfAuthorizedAccount(@PathVariable String jwt) {
        String id = internalService.getIdOfAuthorizedAccount(jwt);
        return ResponseEntity.ok(id);
    }
}
