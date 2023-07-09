package com.example.authentication.service;

import com.example.authentication.IntegrationTestBase;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CacheServiceTest extends IntegrationTestBase {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheService cacheService;
    private final Gson gson;

    @Test
    public void storeInCacheTest() {
        String key = "key";
        String value = "value";
        cacheService.storeInCache(key, value, Duration.ofHours(1));

        String valueFromCache = gson.fromJson((String) redisTemplate.opsForValue().get(key), String.class);
        assertThat(valueFromCache).isNotNull();
        assertThat(valueFromCache).isEqualTo(value);

        valueFromCache = cacheService.getFromCache(key, String.class);
        assertThat(valueFromCache).isEqualTo(value);

        cacheService.deleteFromCache(key);
        Object deletedValueFromCache = redisTemplate.opsForValue().get(key);
        assertThat(deletedValueFromCache).isNull();
    }
}
