package integration_tests.service;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.service.CachingService;
import org.springframework.beans.factory.annotation.Qualifier;
import test_util.annotation.DisableDatabaseAutoConfiguration;
import test_util.annotation.DisableKafkaAutoConfiguration;
import test_util.starter.ConfigServerStarter;
import test_util.starter.RedisStarter;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;

import static com.example.authentication.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AuthenticationApplication.class)
@ActiveProfiles("test")
@DisableKafkaAutoConfiguration
@DisableDatabaseAutoConfiguration
@ExtendWith(InstancioExtension.class)
public class RedisCachingServiceIntegrationTests implements RedisStarter, ConfigServerStarter {

    @Autowired
    @Qualifier("redis")
    private CachingService cachingService;
    @Autowired
    private ValueOperations<String, String> valueOperations;

    @ParameterizedTest
    @InstancioSource
    void shouldStoreValueInCacheMappedWithGivenKey(String key, String value) {
        cachingService.storeInCache(key, value, Duration.ofHours(1));

        String jsonFromCache = valueOperations.get(key);

        assertThat(GSON.fromJson(jsonFromCache, String.class)).isEqualTo(value);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldDeleteMappingFromCache_whenMappingExists(String key, String value) {
        valueOperations.set(key, value);

        cachingService.deleteFromCache(key);

        assertThat(valueOperations.get(key)).isNull();
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnValueFromCache_whenMappingExists(String key, String value) {
        valueOperations.set(key, value);

        Optional<String> optionalValue = cachingService.getFromCache(key, String.class);

        assertThat(optionalValue).contains(value);
    }
}
