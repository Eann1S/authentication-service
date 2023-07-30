package com.example.authentication.constant;

public class UrlConstants {

    private static final String EMAIL_AUTHENTICATION_URL_PREFIX = "/api/v1/authentication/email/%s";
    private static final String EMAIL_ACTIVATION_URL_PREFIX = "/api/v1/activation/email/%s";
    public static final String REGISTER_URL = EMAIL_AUTHENTICATION_URL_PREFIX.formatted("register");
    public static final String LOGIN_URL = EMAIL_AUTHENTICATION_URL_PREFIX.formatted("login");
    public static final String SEND_ACTIVATION_CODE_URL = EMAIL_ACTIVATION_URL_PREFIX.formatted("send-code");
    public static final String CONFIRM_EMAIL_URL = EMAIL_ACTIVATION_URL_PREFIX.formatted("{activationCode}");
    public static final String LOGOUT_URL = "/api/v1/logout";
    public static final String INTERNAL_URL_BASE = "/internal/%s";
    public static final String USER_EMAIL_URL = INTERNAL_URL_BASE.formatted("email/{jwt}");

    private UrlConstants() {
    }
}
