package com.example.authentication.dto.message;

import com.example.authentication.constants.Operation;
import lombok.Builder;

@Builder
public record Message<T> (
        T entity,
        String operation
) {

    public static <T> Message<T> valueOf(T entity, Operation operation) {
        MessageBuilder<T> builder = Message.builder();
        return builder
                .entity(entity)
                .operation(operation.getOperation())
                .build();
    }
}
