package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.dto.request.EmailLoginRequest;
import com.example.authentication.dto.response.JwtDto;
import com.example.authentication.entity.Account;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.TestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.json.JsonConverter.fromJson;
import static com.example.authentication.json.JsonConverter.toJson;
import static com.example.authentication.message.ErrorMessage.INVALID_EMAIL_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.EMAIL_LOGIN_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, TestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class EmailLoginControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAccountUtil testAccountUtil;

    @ParameterizedTest
    @InstancioSource
    void shouldLoginIntoAccount_whenRequestIsValid(Account account) throws Exception {
        testAccountUtil.registerAccount(account);
        EmailLoginRequest request = EmailLoginRequest.of(account.getEmail(), account.getPassword());

        String jsonResponse = loginAndExpectStatus(request, OK);

        JwtDto jwtDto = fromJson(jsonResponse, JwtDto.class);
        assertThat(jwtDto.jwt()).isNotNull();
    }

    @ParameterizedTest
    @InstancioSource
    void shouldNotLoginIntoAccount_whenAccountDoesNotExist(Account account) throws Exception {
        EmailLoginRequest request = EmailLoginRequest.of(account.getEmail(), account.getPassword());

        String jsonResponse = loginAndExpectStatus(request, FORBIDDEN);

        assertThat(jsonResponse).contains(INVALID_EMAIL_CREDENTIALS.getMessage());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldNotLoginIntoAccount_whenEmailAndPasswordAreInvalid(Account account, EmailLoginRequest request) throws Exception {
        testAccountUtil.saveAccountToDatabase(account);

        String jsonResponse = loginAndExpectStatus(request, FORBIDDEN);

        assertThat(jsonResponse).contains(INVALID_EMAIL_CREDENTIALS.getMessage());
    }

    private String loginAndExpectStatus(EmailLoginRequest request, HttpStatus status) throws Exception {
        ResultActions resultActions = performLoginRequest(request);
        return getContentWithExpectedStatus(resultActions, status);
    }

    private ResultActions performLoginRequest(EmailLoginRequest request) throws Exception {
        return mockMvc.perform(post(EMAIL_LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));
    }
}
