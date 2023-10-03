package com.example.authentication.service.strategy.cache_key_strategy;

import com.example.authentication.entity.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("email_confirmation_code")
public class EmailConfirmationCodeCacheKeyFormattingStrategy implements CacheKeyFormattingStrategy {

    private static final String EMAIL_CONFIRMATION_CODE_CACHE_PREFIX = "email_confirmation_code_%s:";

    @Override
    public String formatCacheKey(Account account) {
        return EMAIL_CONFIRMATION_CODE_CACHE_PREFIX.formatted(account.getId());
    }
}
