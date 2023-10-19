package integration_tests.controller.internal;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.entity.Account;
import com.example.authentication.service.JwtService;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test_util.IntegrationTestAccountUtil;
import test_util.starter.AllServicesStarter;

import static com.example.authentication.message.ErrorMessage.INVALID_AUTH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.UrlConstants.ID_OF_AUTHORIZED_ACCOUNT_URL;

@SpringBootTest(classes = {AuthenticationApplication.class, IntegrationTestAccountUtil.class})
@ActiveProfiles("test")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class InternalControllerIntegrationTests implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IntegrationTestAccountUtil integrationTestAccountUtil;

    @ParameterizedTest
    @InstancioSource
    void shouldReturnIdOfAuthorizedAccount_whenAccountExists(Account account) throws Exception {
        account = integrationTestAccountUtil.saveAccountToDatabase(account);
        String jwt = jwtService.createJwtFor(account);

        String response = requestIdOfAuthorizedAccountAndExpectStatus(jwt, OK);

        assertThat(response).isEqualTo(account.getId().toString());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnErrorResponse_whenJwtIsInvalid(String jwt) throws Exception {
        String response = requestIdOfAuthorizedAccountAndExpectStatus(jwt, FORBIDDEN);

        assertThat(response).contains(INVALID_AUTH_TOKEN.getMessage());
    }

    private String requestIdOfAuthorizedAccountAndExpectStatus(String jwt, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(ID_OF_AUTHORIZED_ACCOUNT_URL, jwt));
        return getContentWithExpectedStatus(resultActions, status);
    }
}
