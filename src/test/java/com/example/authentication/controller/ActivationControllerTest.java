package com.example.authentication.controller;

import com.example.authentication.IntegrationTestBase;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.CacheService;
import com.example.authentication.service.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.authentication.constant.CachePrefix.ACTIVATION_CODE_CACHE_PREFIX;
import static com.example.authentication.constant.GlobalConstants.TEST_EMAIL;
import static com.example.authentication.constant.UrlConstants.CONFIRM_EMAIL_URL;
import static com.example.authentication.constant.UrlConstants.SEND_ACTIVATION_CODE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AutoConfigureMockMvc
public class ActivationControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final MessageGenerator messageGenerator;
    private final AccountService accountService;
    private final CacheService cacheService;

    @Test
    public void sendActivationCodeAndConfirmEmailTest() throws Exception {
        registerTestAccountWithDefaults();

        performRequest(
                mockMvc,
                post(SEND_ACTIVATION_CODE_URL)
                        .header("User-Email", TEST_EMAIL),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.send", TEST_EMAIL))
        );
        Optional<String> activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode.isPresent()).isTrue();

        performRequest(
                mockMvc,
                post(CONFIRM_EMAIL_URL, activationCode.get())
                        .header("User-Email", TEST_EMAIL),
                status().isOk(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("activation.success"))
        );
        assertThat(accountService.getAccountByEmail(TEST_EMAIL).isEmailConfirmed()).isTrue();

        activationCode = cacheService.getFromCache(ACTIVATION_CODE_CACHE_PREFIX.formatted(TEST_EMAIL), String.class);
        assertThat(activationCode.isPresent()).isFalse();

        performRequest(
                mockMvc,
                post(CONFIRM_EMAIL_URL, "invalid activation code")
                        .header("User-Email", TEST_EMAIL),
                status().isBadRequest(),
                jsonPath("$.message")
                        .value(messageGenerator.generateMessage("error.activation-code.invalid"))
        );
    }
}
