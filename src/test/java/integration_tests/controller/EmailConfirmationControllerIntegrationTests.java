package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.entity.Account;
import com.example.authentication.service.ConfirmationCodeService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.TestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.message.ErrorMessage.INVALID_CONFIRMATION_CODE;
import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMATION_CODE_SENT;
import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.CONFIRM_EMAIL_URL;
import static test_util.constant.UrlConstants.SEND_EMAIL_CONFIRMATION_CODE_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, TestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class EmailConfirmationControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAccountUtil testAccountUtil;
    @Autowired
    @SpyBean
    private ConfirmationCodeService confirmationCodeService;

    @ParameterizedTest
    @InstancioSource
    void shouldSendConfirmationCodeByEmail_whenAccountExists(Account account) throws Exception {
        account = testAccountUtil.saveAccountToDatabase(account);
        doNothing().when(confirmationCodeService).sendConfirmationCodeFor(eq(account), anyString());

        String jsonResponse = sendConfirmationCodeAndExpectStatus(account.getId(), OK);

        assertThat(jsonResponse).contains(EMAIL_CONFIRMATION_CODE_SENT.getMessage());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldConfirmEmail_whenConfirmationCodeIsValid(Account account) throws Exception {
        account = testAccountUtil.saveAccountToDatabase(account);
        String confirmationCode = confirmationCodeService.createConfirmationCodeFor(account);

        String jsonResponse = confirmEmailAndExpectStatus(account.getId(), confirmationCode, OK);

        assertThat(jsonResponse).contains(EMAIL_CONFIRMED.getMessage());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnErrorResponse_whenConfirmationCodeIsInvalid(Account account, String confirmationCode) throws Exception {
        account = testAccountUtil.saveAccountToDatabase(account);

        String jsonResponse = confirmEmailAndExpectStatus(account.getId(), confirmationCode, BAD_REQUEST);

        assertThat(jsonResponse).contains(INVALID_CONFIRMATION_CODE.getMessage());
    }

    private String sendConfirmationCodeAndExpectStatus(Long userId, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SEND_EMAIL_CONFIRMATION_CODE_URL)
                .header("User-Id", userId));
        return getContentWithExpectedStatus(resultActions, status);
    }

    private String confirmEmailAndExpectStatus(Long userId, String confirmationCode, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(CONFIRM_EMAIL_URL, confirmationCode)
                .header("User-Id", userId));
        return getContentWithExpectedStatus(resultActions, status);
    }
}
