package test_util.annotation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@MockBean({KafkaAdmin.class, KafkaTemplate.class})
public @interface DisableKafkaAutoConfiguration {
}
