package integration_tests.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.entity.Account;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.IntegrationTestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.message.InfoMessage.LOGGED_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.LOGOUT_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, IntegrationTestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class LogoutControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IntegrationTestAccountUtil integrationTestAccountUtil;

    @ParameterizedTest
    @InstancioSource
    void shouldLogoutFromAccount_whenAccountExists(Account account) throws Exception {
        account = integrationTestAccountUtil.saveAccountToDatabase(account);

        String jsonResponse = logoutAndExpectStatus(account, OK);

        assertThat(jsonResponse).contains(LOGGED_OUT.getMessage());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private String logoutAndExpectStatus(Account account, HttpStatus status) throws Exception {
        ResultActions resultActions = performLogoutRequest(account);
        return getContentWithExpectedStatus(resultActions, status);
    }

    private ResultActions performLogoutRequest(Account account) throws Exception {
        return mockMvc.perform(post(LOGOUT_URL)
                .header("User-Email", account.getEmail()));
    }
}
