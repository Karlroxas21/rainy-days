package com.rainydays_engine.rainydays.errors;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message) {
        super(message, 400, "INVALID_REQUEST");
    }
}
