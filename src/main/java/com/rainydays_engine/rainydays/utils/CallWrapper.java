package com.rainydays_engine.rainydays.utils;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CallWrapper {
    // Sync Call
    public static <T> CallResult<T> syncCall(Supplier<T> func) {
        try {
            return CallResult.success(func.get());
        } catch (Throwable error) {
            return CallResult.failure(error);
        }
    }
    /**
     * Usage:
     *
     * CallResult<String> result = CallWrapper.syncCall(() -> {
     *     // some risky operation
     *     return "Hello, World!";
     * });
     *
     * if (result.isSuccess()) {
     *     System.out.println("Result: " + result.getResult());
     * } else {
     *     System.err.println("Error: " + result.getError().getMessage());
     * }
     */

    // Async Call
    public static <T> CompletableFuture<CallResult<T>> asyncCall(Supplier<T> func) {
        return CompletableFuture.supplyAsync(() -> syncCall(func));
    }

    /**
     * Usage:
     *
     * CallWrapper.asyncCall(() -> {
     *     // simulate slow call (e.g., external API)
     *     Thread.sleep(500);
     *     return "Async result";
     * }).thenAccept(result -> {
     *     if (result.isSuccess()) {
     *         System.out.println("Async result: " + result.getResult());
     *     } else {
     *         System.err.println("Async error: " + result.getError().getMessage());
     *     }
     * });
     */
}

class CallResult<T> {
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

    private boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    private T getResult() {
        return result;
    }

    private Throwable getError() {
        return error;
    }
}