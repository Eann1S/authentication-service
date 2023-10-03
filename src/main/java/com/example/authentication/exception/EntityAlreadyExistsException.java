package com.example.authentication.exception;

import static com.example.authentication.message.ErrorMessage.ENTITY_ALREADY_EXISTS;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Object propertyOfEntity) {
        super(ENTITY_ALREADY_EXISTS.formatWith(propertyOfEntity));
    }
}
