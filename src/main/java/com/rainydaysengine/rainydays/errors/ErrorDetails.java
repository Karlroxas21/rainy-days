package com.rainydaysengine.rainydays.errors;

import org.springframework.http.HttpStatus;

public record ErrorDetails(
        String code,
        String message,
        HttpStatus httpStatus,
        Object details,
        Integer lockoutTimeSeconds
) {
}
