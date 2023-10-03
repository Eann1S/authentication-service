package test_util.constant;

public class UrlConstants {

    public static final String REGISTER_URL = "/api/v1/register";
    public static final String EMAIL_LOGIN_URL = "/api/v1/login/email";
    public static final String LOGOUT_URL = "/api/v1/logout";
    public static final String SEND_EMAIL_CONFIRMATION_CODE_URL = "/api/v1/confirmation/email/send-code";
    public static final String CONFIRM_EMAIL_URL = "/api/v1/confirmation/email/{confirmationCode}";
    public static final String ID_OF_AUTHORIZED_ACCOUNT_URL = "/internal/account/id/{jwt}";

    private UrlConstants() {
    }
}
