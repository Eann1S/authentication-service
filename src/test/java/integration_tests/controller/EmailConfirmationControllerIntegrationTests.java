package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.entity.Account;
import com.example.authentication.service.ConfirmationCodeService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.IntegrationTestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.message.ErrorMessage.INVALID_CONFIRMATION_CODE;
import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.CONFIRM_EMAIL_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, IntegrationTestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class EmailConfirmationControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IntegrationTestAccountUtil integrationTestAccountUtil;
    @Autowired
    @Qualifier("email")
    private ConfirmationCodeService confirmationCodeService;

    @ParameterizedTest
    @InstancioSource
    void shouldConfirmEmail_whenConfirmationCodeIsValid(Account account) throws Exception {
        account = integrationTestAccountUtil.saveAccountToDatabase(account);
        String confirmationCode = confirmationCodeService.generateConfirmationCodeFor(account);

        String jsonResponse = confirmEmailAndExpectStatus(account, confirmationCode, OK);

        assertThat(jsonResponse).contains(EMAIL_CONFIRMED.getMessage());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnErrorResponse_whenConfirmationCodeIsInvalid(Account account, String confirmationCode) throws Exception {
        account = integrationTestAccountUtil.saveAccountToDatabase(account);

        String jsonResponse = confirmEmailAndExpectStatus(account, confirmationCode, BAD_REQUEST);

        assertThat(jsonResponse).contains(INVALID_CONFIRMATION_CODE.getMessage());
    }

    private String confirmEmailAndExpectStatus(Account account, String confirmationCode, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(CONFIRM_EMAIL_URL, confirmationCode)
                .header("User-Email", account.getEmail()));
        return getContentWithExpectedStatus(resultActions, status);
    }
}
