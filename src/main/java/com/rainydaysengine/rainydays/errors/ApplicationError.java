package com.rainydaysengine.rainydays.errors;

import org.springframework.http.HttpStatus;


public class ApplicationError extends RuntimeException {

    private final ErrorDetails errorDetails;


    private ApplicationError(ErrorDetails errorDetails) {
        super(errorDetails.message());
        this.errorDetails = errorDetails;
    }

    public static ApplicationError InternalError(Object details) {
        return new ApplicationError(new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
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
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                details,
                null
        )
        );
    }

    public static ApplicationError Unauthorized(Object details) {
        return new ApplicationError(new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized Request",
                HttpStatus.UNAUTHORIZED,
                details,
                null
        )
        );
    }

    public static ApplicationError NotFound(Object details) {
        return new ApplicationError(new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                HttpStatus.NOT_FOUND,
                details,
                null
        )
        );
    }

    public static ApplicationError Conflict(Object details) {
        return new ApplicationError(new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "Duplicate",
                HttpStatus.FORBIDDEN,
                details,
                null
        )
        );
    }

    public static ApplicationError tooManyRequest(String message, long seconds) {
        return new ApplicationError(new ErrorDetails(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                message,
                HttpStatus.TOO_MANY_REQUESTS,
                null,
                seconds
        )
        );
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}
