package com.example.authentication.exception;

import com.example.authentication.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        return generateErrorResponse(e, HttpStatus.NOT_FOUND);
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
