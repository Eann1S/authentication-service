package com.example.authentication.controller.internal;

import com.example.authentication.exception.ExpiredAuthenticationTokenException;
import com.example.authentication.exception.InvalidAuthenticationTokenException;
import com.example.authentication.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final JwtService jwtService;

    @GetMapping("/email/{jwt}")
    public ResponseEntity<String> getUserEmail(@PathVariable String jwt) {
        return ResponseEntity.ok(jwtService.getUserEmailByJwt(jwt));
    }

    @RestControllerAdvice(basePackageClasses = InternalController.class, assignableTypes = InternalController.class)
    static class InternalExceptionHandler {
        @ExceptionHandler({
                InvalidAuthenticationTokenException.class,
                ExpiredAuthenticationTokenException.class
        })
        public ResponseEntity<String> handleForbiddenException(Exception e) {
            return generateErrorResponse(e, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleNotFoundException(Exception e) {
            return generateErrorResponse(e, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            return generateErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        private ResponseEntity<String> generateErrorResponse(Exception e, HttpStatus status) {
            return new ResponseEntity<>(e.getMessage(), status);
        }
    }
}
