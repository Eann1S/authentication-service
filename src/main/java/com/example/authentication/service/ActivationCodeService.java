package com.example.authentication.service;

import com.example.authentication.dto.response.MessageResponse;
import com.example.authentication.exception.InvalidActivationCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static com.example.authentication.constant.CachePrefix.ACTIVATION_CODE_CACHE_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationCodeService {

    private final CacheService cacheService;
    private final AccountService accountService;
    private final MessageGenerator messageGenerator;
    private final EmailService emailService;

    public ResponseEntity<MessageResponse> sendActivationCodeByEmail(String email) {
        String activationCode = UUID.randomUUID().toString();
        cacheService.storeInCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(email), activationCode, Duration.ofHours(2));

        emailService.sendMessageByEmail(email, activationCode);
        log.info("activation code was sent to {}", email);

        return ResponseEntity.ok(
                new MessageResponse(messageGenerator.generateMessage("activation.send", email))
        );
    }

    public ResponseEntity<MessageResponse> confirmEmail(String email, String activationCode) {
        String cacheKey = ACTIVATION_CODE_CACHE_PREFIX.formatted(email);
        Optional<String> activationCodeFromCache = cacheService.getFromCache(cacheKey, String.class);
        if (activationCodeFromCache.isEmpty() || !StringUtils.equals(activationCode, activationCodeFromCache.get())) {
            throw new InvalidActivationCodeException(messageGenerator.generateMessage("error.activation-code.invalid"));
        }

        cacheService.deleteFromCache(cacheKey);
        accountService.confirmAccountEmail(email);
        log.info("email {} was confirmed", email);

        return ResponseEntity.ok(
                new MessageResponse(messageGenerator.generateMessage("activation.success"))
        );
    }
}
