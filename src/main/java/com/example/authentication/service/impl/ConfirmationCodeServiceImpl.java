package com.example.authentication.service.impl;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.InvalidConfirmationCodeException;
import com.example.authentication.service.CachingService;
import com.example.authentication.service.ConfirmationCodeService;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import com.example.authentication.service.strategy.code_generation_strategy.ConfirmationCodeGenerationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private static final Duration CONFIRMATION_CODE_DURATION_EXPIRATION = Duration.ofHours(2);
    private final CachingService cachingService;
    private final CacheKeyFormattingStrategy cacheKeyFormattingStrategy;
    private final ConfirmationCodeGenerationStrategy confirmationCodeGenerationStrategy;

    @Override
    public String generateConfirmationCodeFor(Account account) {
        String confirmationCode = confirmationCodeGenerationStrategy.generateConfirmationCode();
        storeConfirmationCodeOfAccountInCache(account, confirmationCode);
        log.info("confirmation code for account {} was created", account.getId());
        return confirmationCode;
    }

    @Override
    public void invalidateConfirmationCodeOf(Account account) {
        deleteConfirmationCodeOfAccountFromCache(account);
        log.info("confirmation code for account {} was invalidated", account.getId());
    }

    @Override
    public boolean isConfirmationCodeOfAccountValid(Account account, String confirmationCode) {
        String actualConfirmationCode = getConfirmationCodeOfAccountFromCache(account);
        return StringUtils.equals(actualConfirmationCode, confirmationCode);
    }

    private void storeConfirmationCodeOfAccountInCache(Account account, String confirmationCode) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        cachingService.storeInCache(cacheKey, confirmationCode, CONFIRMATION_CODE_DURATION_EXPIRATION);
    }

    private String getConfirmationCodeOfAccountFromCache(Account account) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        return cachingService.getFromCache(cacheKey, String.class)
                .orElseThrow(InvalidConfirmationCodeException::new);
    }

    private void deleteConfirmationCodeOfAccountFromCache(Account account) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        cachingService.deleteFromCache(cacheKey);
    }
}
