package com.example.authentication.integration_tests.constants;

public class UrlConstants {

    private static final String BASE_AUTH_CONTROLLER_PREFIX = "/api/v1/auth/email/%s";
    public static final String REGISTER_URL = BASE_AUTH_CONTROLLER_PREFIX.formatted("register");
    public static final String LOGIN_URL = BASE_AUTH_CONTROLLER_PREFIX.formatted("login");
    public static final String SEND_ACTIVATION_CODE_URL = BASE_AUTH_CONTROLLER_PREFIX.formatted("send");
    public static final String CONFIRM_EMAIL_URL = BASE_AUTH_CONTROLLER_PREFIX.formatted("confirm/{activationCode}");
    public static final String LOGOUT_URL = "/api/v1/auth/logout";

    private UrlConstants() {
    }
}
