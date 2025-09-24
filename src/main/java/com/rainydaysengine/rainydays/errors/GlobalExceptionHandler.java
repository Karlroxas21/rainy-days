package com.rainydaysengine.rainydays.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationError.class)
    public ResponseEntity<ErrorDetails> handleApplicationException(ApplicationError ex) {
        return new ResponseEntity<>(ex.getErrorDetails(), ex.getErrorDetails().httpStatus());
    }

    // Catch all for unknown errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericError(Exception ex) {
        return new ResponseEntity<>(
                new ErrorDetails("INTERNAL_ERROR", ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        null,
                        null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Example: wrap into your custom format
        return ResponseEntity.badRequest().body(errors);
    }

}
