package com.example.authentication.service.caching;

import java.time.Duration;
import java.util.Optional;

public interface CachingService {

    <T> void storeInCache(String key, T value, Duration duration);

    <T> Optional<T> getFromCache(String key, Class<T> type);

    void deleteFromCache(String key);
}
