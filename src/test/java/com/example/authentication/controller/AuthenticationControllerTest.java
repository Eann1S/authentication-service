package com.example.authentication.controller;

import com.example.authentication.IntegrationTestBase;
import com.example.authentication.dto.message.UserMessage;
import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.authentication.constant.CachePrefix.ACTIVATION_CODE_CACHE_PREFIX;
import static com.example.authentication.constant.CachePrefix.JWT_CACHE_PREFIX;
import static com.example.authentication.constant.GlobalConstants.*;
import static com.example.authentication.constant.UrlConstants.*;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AutoConfigureMockMvc
@SuppressWarnings("all")
public class AuthenticationControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MessageGenerator messageGenerator;
    private final MessageSource testMessageSource;
    private final AccountService accountService;
    private final AuthenticationService authenticationService;
    private final CacheService cacheService;
    private final TestKafkaConsumerService testKafkaConsumerService;
    private final Gson gson;
    @MockBean
    private final EmailService emailService;

    @Test
    public void registerTest() throws Exception {
        EmailRegisterRequest registerRequest = new EmailRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("account.creation.success", registerRequest.email()))
        );
        assertThat(accountService.isAccountExistsByEmail(registerRequest.email())).isTrue();

        testKafkaConsumerService.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(testKafkaConsumerService.getMessagePayload()).isNotNull();

        UserMessage userMessage = gson.fromJson(testKafkaConsumerService.getMessagePayload(), UserMessage.class);
        assertThat(userMessage.user().email()).isEqualTo(registerRequest.email());
        assertThat(userMessage.user().username()).isEqualTo(registerRequest.username());

        performRequest(
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
        registerTestAccountWithDefaults(authenticationService::registerWithEmail);
        EmailLoginRequest loginRequest = new EmailLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                status().isOk(),
                jsonPath("$.jwt").exists()
        );
        String jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt).isNotNull();

        loginRequest = new EmailLoginRequest(TEST_EMAIL, "invalid password");
        performRequest(
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                status().isForbidden(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.account.invalid_credentials"))
        );

        performRequest(
                post(LOGOUT_URL)
                        .header(AUTHORIZATION, "Bearer " + jwt),
                status().isMovedTemporarily(),
                content().string("")
        );
        jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt).isNull();
    }

    @Test
    public void sendActivationCodeAndConfirmEmailTest() throws Exception {
        registerTestAccountWithDefaults(authenticationService::registerWithEmail);

        performRequest(
                post(SEND_ACTIVATION_CODE_URL)
                        .header("userEmail", TEST_EMAIL),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.send", TEST_EMAIL))
        );
        String activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode).isNotNull();

        performRequest(
                post(CONFIRM_EMAIL_URL, activationCode)
                        .header("userEmail", TEST_EMAIL),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.success"))
        );
        assertThat(accountService.getAccountByEmail(TEST_EMAIL).isEmailConfirmed()).isTrue();

        activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode).isNull();

        performRequest(
                post(CONFIRM_EMAIL_URL, "invalid activation code")
                        .header("userEmail", TEST_EMAIL),
                status().isBadRequest(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.activation-code.invalid"))
        );
    }

    private void performRequest(
            MockHttpServletRequestBuilder requestBuilder,
            ResultMatcher... resultMatcher
    ) throws Exception {
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        expectResult(resultActions, resultMatcher);
    }

    private void expectResult(ResultActions resultActions, ResultMatcher... resultMatcher) throws Exception {
        resultActions.andExpectAll(resultMatcher);
    }
}
