package com.example.authentication.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Operation {

    ADD("ADD"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String operation;
}
