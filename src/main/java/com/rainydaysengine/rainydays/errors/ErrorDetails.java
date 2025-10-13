package com.rainydaysengine.rainydays.errors;

import org.springframework.http.HttpStatus;

public record ErrorDetails(
        int code,
        String message,
        HttpStatus httpStatus,
        Object details,
        Long lockoutTimeSeconds
) {
}
