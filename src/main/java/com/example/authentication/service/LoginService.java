package com.example.authentication.service;

public interface LoginService<T> {

    String login(T loginRequest);
}
