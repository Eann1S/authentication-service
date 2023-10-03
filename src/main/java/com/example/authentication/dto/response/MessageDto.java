package com.example.authentication.dto.response;

import com.example.authentication.message.InfoMessage;

public record MessageDto(
        String message
) {

    public static MessageDto of(InfoMessage infoMessage) {
        return new MessageDto(infoMessage.getMessage());
    }
}
