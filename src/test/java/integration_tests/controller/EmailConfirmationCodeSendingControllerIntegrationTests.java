package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.entity.Account;
import com.example.authentication.service.ConfirmationCodeSendingService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.IntegrationTestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.message.InfoMessage.EMAIL_CONFIRMATION_CODE_SENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.SEND_EMAIL_CONFIRMATION_CODE_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, IntegrationTestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class EmailConfirmationCodeSendingControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IntegrationTestAccountUtil integrationTestAccountUtil;
    @MockBean
    @Qualifier("email")
    private ConfirmationCodeSendingService confirmationCodeSendingService;

    @ParameterizedTest
    @InstancioSource
    void shouldSendConfirmationCodeByEmail_whenAccountExists(Account account) throws Exception {
        account = integrationTestAccountUtil.saveAccountToDatabase(account);

        String jsonResponse = sendConfirmationCodeAndExpectStatus(account.getId(), OK);

        assertThat(jsonResponse).contains(EMAIL_CONFIRMATION_CODE_SENT.getMessage());
        verify(confirmationCodeSendingService).sendConfirmationCodeForAccountWithId(account.getId());
    }

    private String sendConfirmationCodeAndExpectStatus(Long userId, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SEND_EMAIL_CONFIRMATION_CODE_URL)
                .header("User-Id", userId));
        return getContentWithExpectedStatus(resultActions, status);
    }
}
