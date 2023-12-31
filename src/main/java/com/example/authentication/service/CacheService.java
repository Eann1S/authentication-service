package com.example.authentication.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson;

    public void deleteFromCache(String key) {
        redisTemplate.delete(key);
    }

    public <T> void storeInCache(String key, T value, Type type, Duration duration) {
        String json = gson.toJson(value, type);
        redisTemplate.opsForValue().set(key, json, duration);
    }

    public <T> void storeInCache(String key, T value, Duration duration) {
        String json = gson.toJson(value);
        redisTemplate.opsForValue().set(key, json, duration);
    }

    @Nullable
    public <T> T getFromCache(String key, TypeToken<T> type) {
        String valueFromCache = (String) redisTemplate.opsForValue().get(key);
        return gson.fromJson(valueFromCache, type);
    }

    @Nullable
    public <T> T getFromCache(String key, Class<T> type) {
        String valueFromCache = (String) redisTemplate.opsForValue().get(key);
        return gson.fromJson(valueFromCache, type);
    }
}
