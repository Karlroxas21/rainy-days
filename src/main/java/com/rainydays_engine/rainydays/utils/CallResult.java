package com.rainydays_engine.rainydays.utils;

public class CallResult<T> {
    private final T result;
    private final Throwable error;

    private CallResult(T result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    public static <T> CallResult<T> success(T result) {
        return new CallResult<>(result, null);
    }

    public static <T> CallResult<T> failure(Throwable error) {
        return new CallResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T getResult() {
        return result;
    }

    public Throwable getError() {
        return error;
    }
}