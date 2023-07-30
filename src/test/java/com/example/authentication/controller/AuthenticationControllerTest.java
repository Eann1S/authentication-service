package com.example.authentication.controller;

import com.example.authentication.IntegrationTestBase;
import com.example.authentication.dto.message.UserMessage;
import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.CacheService;
import com.example.authentication.service.MessageGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.authentication.constant.CachePrefix.JWT_CACHE_PREFIX;
import static com.example.authentication.constant.GlobalConstants.*;
import static com.example.authentication.constant.UrlConstants.*;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AutoConfigureMockMvc
public class AuthenticationControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MessageGenerator messageGenerator;
    private final MessageSource testMessageSource;
    private final AccountService accountService;
    private final CacheService cacheService;
    private final TestKafkaConsumerService testKafkaConsumerService;
    private final Gson gson;

    @Test
    public void registerTest() throws Exception {
        EmailRegisterRequest registerRequest = new EmailRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                mockMvc,
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("account.creation.success", registerRequest.email()))
        );
        assertThat(accountService.isAccountExistsByEmail(registerRequest.email())).isTrue();

        boolean await = testKafkaConsumerService.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(await).isTrue();
        assertThat(testKafkaConsumerService.getMessagePayload()).isNotNull();

        UserMessage userMessage = gson.fromJson(testKafkaConsumerService.getMessagePayload(), UserMessage.class);
        assertThat(userMessage.user().email()).isEqualTo(registerRequest.email());
        assertThat(userMessage.user().username()).isEqualTo(registerRequest.username());

        performRequest(
                mockMvc,
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                status().isBadRequest(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.entity.already_exists", registerRequest.email()))
        );
    }

    @Test
    public void validationTest() throws Exception {
        EmailRegisterRequest registerRequest = new EmailRegisterRequest(" ", "", "123");

        performRequest(
                mockMvc,
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                status().isBadRequest(),
                jsonPath("$.username")
                        .value(testMessageSource.getMessage("username.not_blank", null, Locale.getDefault())),
                jsonPath("$.email")
                        .value(testMessageSource.getMessage("email.not_blank", null, Locale.getDefault())),
                jsonPath("$.password")
                        .value(testMessageSource.getMessage("password.size", new Object[]{8, 25}, Locale.getDefault()))
        );

        registerRequest = new EmailRegisterRequest(TEST_USERNAME, "invalid email", "          ");
        performRequest(
                mockMvc,
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                status().isBadRequest(),
                jsonPath("$.email")
                        .value(testMessageSource.getMessage("email.invalid", null, Locale.getDefault())),
                jsonPath("$.password")
                        .value(testMessageSource.getMessage("password.not_blank", null, Locale.getDefault()))
        );

        EmailLoginRequest loginRequest = new EmailLoginRequest("invalid email", "   ");
        performRequest(
                mockMvc,
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                status().isBadRequest(),
                jsonPath("$.email")
                        .value(testMessageSource.getMessage("email.invalid", null, Locale.getDefault())),
                jsonPath("$.password")
                        .value(testMessageSource.getMessage("password.not_blank", null, Locale.getDefault()))
        );
    }

    @Test
    public void loginTest() throws Exception {
        registerTestAccountWithDefaults();
        EmailLoginRequest loginRequest = new EmailLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                mockMvc,
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                status().isOk(),
                jsonPath("$.jwt").exists()
        );
        Optional<String> jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt.isPresent()).isTrue();

        loginRequest = new EmailLoginRequest(TEST_EMAIL, "invalid password");
        performRequest(
                mockMvc,
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                status().isForbidden(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.account.invalid_credentials"))
        );

        performRequest(
                mockMvc,
                post(LOGOUT_URL)
                        .header(AUTHORIZATION, "Bearer " + jwt.get()),
                status().isOk(),
                content().string("")
        );
        jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt.isPresent()).isFalse();
    }
}
