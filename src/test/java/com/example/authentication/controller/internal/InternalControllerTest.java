package com.example.authentication.controller.internal;

import com.example.authentication.IntegrationTestBase;
import com.example.authentication.service.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.authentication.constant.UrlConstants.USER_EMAIL_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InternalControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final MessageGenerator messageGenerator;

    @Test
    public void getUserEmailTest() throws Exception {
        performRequest(
                mockMvc,
                get(USER_EMAIL_URL, "invalid jwt"),
                status().isForbidden(),
                content().string(messageGenerator.generateMessage("error.auth-token.invalid"))
        );
    }
}
