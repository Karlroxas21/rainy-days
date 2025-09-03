package com.rainydays_engine.rainydays.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

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

}
