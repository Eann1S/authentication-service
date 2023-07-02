package com.example.authentication.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final Gson gson;
    private final RedisTemplate<String, Object> redisTemplate;

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
