package com.rainydaysengine.rainydays.errors;

import org.springframework.http.HttpStatus;

public class ApplicationError extends RuntimeException {

    private final ErrorDetails errorDetails;


    private ApplicationError(ErrorDetails errorDetails) {
        super(errorDetails.message());
        this.errorDetails = errorDetails;
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    public static ApplicationError InternalError(Object details) {
        return new ApplicationError(new ErrorDetails(
                "INTERNAL_ERROR",
                "Internal Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                details,
                null
                )
        );
    }

    public static ApplicationError InternalError() {
        return new ApplicationError(null);
    }

    public static ApplicationError BadRequest(Object details) {
        return new ApplicationError(new ErrorDetails(
                "BAD_REQUEST",
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                details,
                null
        )
        );
    }

    public static ApplicationError Unauthorized(Object details) {
        return new ApplicationError(new ErrorDetails(
                "UNAUTHORIZED",
                "Unauthorized Request",
                HttpStatus.UNAUTHORIZED,
                details,
                null
        )
        );
    }

    public static ApplicationError NotFound(Object details) {
        return new ApplicationError(new ErrorDetails(
                "NOT_FOUND",
                "Not Found",
                HttpStatus.NOT_FOUND,
                details,
                null
        )
        );
    }

    public static ApplicationError Conflict(Object details) {
        return new ApplicationError(new ErrorDetails(
                "CONFLICT",
                "Duplicate",
                HttpStatus.FORBIDDEN,
                details,
                null
        )
        );
    }

    public static ApplicationError tooManyRequest(String message, Object details) {
        return new ApplicationError(new ErrorDetails(
                "TOO_MANY_REQUESTS",
                message,
                HttpStatus.TOO_MANY_REQUESTS,
                details,
                null
        )
        );
    }
}
