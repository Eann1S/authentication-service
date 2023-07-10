package com.example.authentication.exception;

import com.example.authentication.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(field, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

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
