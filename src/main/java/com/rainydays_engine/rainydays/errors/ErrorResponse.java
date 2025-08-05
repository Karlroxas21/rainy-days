package com.rainydays_engine.rainydays.errors;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}
