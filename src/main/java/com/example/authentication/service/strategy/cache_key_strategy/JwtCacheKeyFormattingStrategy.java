package com.example.authentication.service.strategy.cache_key_strategy;

import com.example.authentication.entity.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("jwt")
public class JwtCacheKeyFormattingStrategy implements CacheKeyFormattingStrategy {

    private static final String JWT_CACHE_PREFIX = "jwt_%s:";

    @Override
    public String formatCacheKey(Account account) {
        return JWT_CACHE_PREFIX.formatted(account.getId());
    }
}
