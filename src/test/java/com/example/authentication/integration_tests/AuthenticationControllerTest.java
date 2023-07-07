package com.example.authentication.integration_tests;

import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.example.authentication.constants.CachePrefix.ACTIVATION_CODE_CACHE_PREFIX;
import static com.example.authentication.constants.CachePrefix.JWT_CACHE_PREFIX;
import static com.example.authentication.integration_tests.constants.GlobalConstants.*;
import static com.example.authentication.integration_tests.constants.UrlConstants.*;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AutoConfigureMockMvc
@SuppressWarnings("all")
public class AuthenticationControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MessageGenerator messageGenerator;
    private final AccountService accountService;
    private final AuthenticationService authenticationService;
    private final CacheService cacheService;
    @MockBean
    private final EmailService emailService;

    @Test
    public void registerTest() throws Exception {
        EmailRegisterRequest registerRequest = buildEmailRegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                OK,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("account.creation.success", registerRequest.email()))
        );
        assertThat(accountService.isAccountExistsByEmail(registerRequest.email())).isTrue();

        performRequest(
                post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)),
                BAD_REQUEST,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.entity.already_exists", registerRequest.email()))
        );
    }

    @Test
    public void loginTest() throws Exception {
        registerTestAccount(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
        EmailLoginRequest loginRequest = buildEmailLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                OK,
                jsonPath("$.jwt").exists()
        );
        String jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt).isNotNull();

        loginRequest = buildEmailLoginRequest(TEST_EMAIL, "invalid password");
        performRequest(
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)),
                FORBIDDEN,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.account.invalid_credentials"))
        );

        performRequest(
                post(LOGOUT_URL)
                        .header(AUTHORIZATION, "Bearer " + jwt),
                MOVED_TEMPORARILY,
                content().string("")
        );
        jwt = cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(jwt).isNull();
    }

    @Test
    public void sendActivationCodeAndConfirmEmailTest() throws Exception {
        registerTestAccount(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        performRequest(
                post(SEND_ACTIVATION_CODE_URL)
                        .header("userEmail", TEST_EMAIL),
                OK,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.send", TEST_EMAIL))
        );
        String activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode).isNotNull();

        performRequest(
                post(CONFIRM_EMAIL_URL, activationCode)
                        .header("userEmail", TEST_EMAIL),
                OK,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.success"))
        );
        assertThat(accountService.getAccountByEmail(TEST_EMAIL).isEmailConfirmed()).isTrue();

        activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode).isNull();

        performRequest(
                post(CONFIRM_EMAIL_URL, "invalid activation code")
                        .header("userEmail", TEST_EMAIL),
                BAD_REQUEST,
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.activation-code.invalid"))
        );
    }

    private void performRequest(
            MockHttpServletRequestBuilder requestBuilder,
            HttpStatus status,
            ResultMatcher resultMatcher
    ) throws Exception {
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        expectResult(resultActions, status().is(status.value()), resultMatcher);
    }

    private void expectResult(ResultActions resultActions, ResultMatcher... resultMatcher) throws Exception {
        resultActions.andExpectAll(resultMatcher);
    }

    private void registerTestAccount(String username, String email, String password) {
        authenticationService.registerWithEmail(
                buildEmailRegisterRequest(username, email, password)
        );
    }

    private EmailLoginRequest buildEmailLoginRequest(String email, String password) {
        return EmailLoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    private EmailRegisterRequest buildEmailRegisterRequest(String username, String email, String password) {
        return EmailRegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }
}
