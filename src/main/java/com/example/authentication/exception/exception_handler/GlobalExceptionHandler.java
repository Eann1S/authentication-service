package com.example.authentication.exception.exception_handler;

import com.example.authentication.dto.ErrorDto;
import com.example.authentication.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errorsMap = getErrorsMapFrom(exception);
        return new ResponseEntity<>(errorsMap, BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(AccountNotFoundException exception) {
        return createErrorResponse(exception, NOT_FOUND);
    }

    @ExceptionHandler({
            InvalidAuthenticationTokenException.class,
            ExpiredAuthenticationTokenException.class,
            InvalidEmailCredentialsException.class,
            EmailNotConfirmedException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ErrorDto> handleForbiddenException(Exception exception) {
        return createErrorResponse(exception, FORBIDDEN);
    }

    @ExceptionHandler({
            AccountAlreadyExistsException.class,
            InvalidConfirmationCodeException.class
    })
    public ResponseEntity<ErrorDto> handleBadRequestException(Exception exception) {
        return createErrorResponse(exception, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        return createErrorResponse(exception, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> createErrorResponse(Exception exception, HttpStatus status) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage());
        return new ResponseEntity<>(errorDto, status);
    }

    private Map<String, String> getErrorsMapFrom(MethodArgumentNotValidException exception) {
        List<ObjectError> objectErrors = getObjectErrorsFrom(exception);
        return mapObjectErrorsWithMessages(objectErrors);
    }

    private List<ObjectError> getObjectErrorsFrom(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return bindingResult.getAllErrors();
    }

    @SuppressWarnings("DataFlowIssue")
    private Map<String, String> mapObjectErrorsWithMessages(List<ObjectError> objectErrors) {
        return objectErrors.stream()
                .map(objectError -> (FieldError) objectError)
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
    }
}
