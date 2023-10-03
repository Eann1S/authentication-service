package com.example.authentication.service.strategy.cache_key_strategy;

import com.example.authentication.entity.Account;

public interface CacheKeyFormattingStrategy {

    String formatCacheKey(Account account);
}
