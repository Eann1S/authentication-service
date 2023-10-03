package com.example.authentication.service.login;

public interface LoginService<T> {

    String login(T loginRequest);
}
