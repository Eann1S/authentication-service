package com.example.authentication.service.impl;

import com.example.authentication.service.CachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static com.example.authentication.json.JsonConverter.fromJson;
import static com.example.authentication.json.JsonConverter.toJson;

@Service
@Qualifier("redis")
@RequiredArgsConstructor
public class RedisCachingService implements CachingService {

    private final ValueOperations<String, String> valueOperations;

    @Override
    public void deleteFromCache(String key) {
        valueOperations.getAndDelete(key);
    }

    @Override
    public <T> void storeInCache(String key, T value, Duration duration) {
        valueOperations.set(key, toJson(value), duration);
    }

    @Override
    public <T> Optional<T> getFromCache(String key, Class<T> type) {
        String valueFromCache = valueOperations.get(key);
        T value = fromJson(valueFromCache, type);
        return Optional.ofNullable(value);
    }
}
