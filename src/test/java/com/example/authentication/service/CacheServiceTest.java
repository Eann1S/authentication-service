package com.example.authentication.service;

import com.example.authentication.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CacheServiceTest extends IntegrationTestBase {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheService cacheService;

    @Test
    public void storeInCacheTest() {
        String key = "key";
        String value = "value";
        cacheService.storeInCache(key, value, Duration.ofHours(1));

        Optional<String> valueFromCache = cacheService.getFromCache(key, String.class);
        assertThat(valueFromCache.isPresent()).isTrue();
        assertThat(valueFromCache.get()).isEqualTo(value);

        cacheService.deleteFromCache(key);
        Object deletedValueFromCache = redisTemplate.opsForValue().get(key);
        assertThat(deletedValueFromCache).isNull();
    }
}
