package com.example.authentication.service.caching;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static com.example.authentication.json.JsonConverter.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class RedisCachingServiceTests {

    @Mock
    private ValueOperations<String, String> valueOperations;
    private RedisCachingService redisCachingService;

    @BeforeEach
    void setUp() {
        redisCachingService = new RedisCachingService(valueOperations);
    }

    @ParameterizedTest
    @InstancioSource
    void deleteFromCache(String key) {
        redisCachingService.deleteFromCache(key);

        verify(valueOperations).getAndDelete(key);
    }

    @ParameterizedTest
    @InstancioSource
    void storeInCache(String key, String value) {
        redisCachingService.storeInCache(key, value, Duration.ofHours(1));

        verify(valueOperations).set(key, toJson(value), Duration.ofHours(1));
    }

    @ParameterizedTest
    @InstancioSource
    void getFromCache(String key, String value) {
        when(valueOperations.get(key)).thenReturn(value);

        Optional<String> actualValue = redisCachingService.getFromCache(key, String.class);

        assertThat(actualValue).contains(value);
    }
}