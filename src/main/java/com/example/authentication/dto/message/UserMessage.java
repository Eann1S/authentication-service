package com.example.authentication.dto.message;

import com.example.authentication.constant.Operation;
import com.example.authentication.dto.response.UserResponse;
import lombok.Builder;

@Builder
public record UserMessage (
        UserResponse user,
        String operation
) {

    public static UserMessage valueOf(UserResponse user, Operation operation) {
        return UserMessage.builder()
                .user(user)
                .operation(operation.getOperation())
                .build();
    }
}
