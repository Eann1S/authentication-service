package com.example.authentication.exception;

import com.example.authentication.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        return generateErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            EmptyAuthenticationHeaderException.class
    })
    public ResponseEntity<ErrorResponse> handleForbiddenException(Exception e) {
        return generateErrorResponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            EntityAlreadyExistsException.class,
            InvalidActivationCodeException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        return generateErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return generateErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> generateErrorResponse(Exception e, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.value())
                .message(e.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
