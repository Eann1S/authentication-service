package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.dto.request.RegisterRequest;
import integration_tests.test_config.validator.TestValidatorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.starter.AllServicesStarter;

import java.util.Locale;

import static com.example.authentication.json.JsonConverter.toJson;
import static com.example.authentication.message.ErrorMessage.ACCOUNT_ALREADY_EXISTS;
import static com.example.authentication.message.InfoMessage.ACCOUNT_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.GlobalConstants.*;
import static test_util.constant.UrlConstants.REGISTER_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, TestValidatorConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RegistrationControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    @Qualifier("test")
    private MessageSource messageSource;

    @Test
    void shouldRegisterNewAccount_whenRequestIsValid() throws Exception {
        RegisterRequest request = RegisterRequest.of(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

        String jsonResponse = registerAccountAndExpectStatus(request, OK);

        assertThat(jsonResponse).contains(ACCOUNT_CREATED.getMessage());
    }

    @Test
    void shouldNotRegisterAccount_whenAccountAlreadyExists() throws Exception {
        RegisterRequest request = RegisterRequest.of(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

        registerAccountAndExpectStatus(request, OK);
        String failureJsonResponse = registerAccountAndExpectStatus(request, BAD_REQUEST);

        assertThat(failureJsonResponse).contains(ACCOUNT_ALREADY_EXISTS.formatWith(TEST_EMAIL));
    }

    @Test
    void shouldNotRegisterAccount_whenEmailIsEmpty() throws Exception {
        RegisterRequest request = RegisterRequest.of("", TEST_USERNAME, TEST_PASSWORD);

        String jsonResponse = registerAccountAndExpectStatus(request, BAD_REQUEST);

        String errorMessage = getErrorMessageByCode("email.not_blank");
        assertThat(jsonResponse).contains(errorMessage);
    }

    @Test
    void shouldNotRegisterAccount_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = RegisterRequest.of("invalid", TEST_USERNAME, TEST_PASSWORD);

        String jsonResponse = registerAccountAndExpectStatus(request, BAD_REQUEST);

        String errorMessage = getErrorMessageByCode("email.invalid");
        assertThat(jsonResponse).contains(errorMessage);
    }

    @Test
    void shouldNotRegisterAccount_whenUsernameIsEmpty() throws Exception {
        RegisterRequest request = RegisterRequest.of(TEST_EMAIL, "", TEST_PASSWORD);

        String jsonResponse = registerAccountAndExpectStatus(request, BAD_REQUEST);

        String errorMessage = getErrorMessageByCode("username.not_blank");
        assertThat(jsonResponse).contains(errorMessage);
    }

    @Test
    void shouldNotRegisterAccount_whenPasswordHasInvalidLength() throws Exception {
        RegisterRequest request = RegisterRequest.of(TEST_EMAIL, TEST_USERNAME, "123");

        String jsonResponse = registerAccountAndExpectStatus(request, BAD_REQUEST);

        String errorMessage = getErrorMessageByCode("password.size", 8, 25);
        assertThat(jsonResponse).contains(errorMessage);
    }

    private String getErrorMessageByCode(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    private String registerAccountAndExpectStatus(RegisterRequest request, HttpStatus status) throws Exception {
        ResultActions resultActions = performRegisterRequest(request);
        return getContentWithExpectedStatus(resultActions, status);
    }

    private ResultActions performRegisterRequest(RegisterRequest request) throws Exception {
        return mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));
    }
}
