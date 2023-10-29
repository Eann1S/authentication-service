package com.example.authentication.dto;

import com.example.authentication.message.InfoMessage;

public record InfoMessageDto(
        String message
) {

    public static InfoMessageDto of(InfoMessage infoMessage) {
        return new InfoMessageDto(infoMessage.getMessage());
    }
}
