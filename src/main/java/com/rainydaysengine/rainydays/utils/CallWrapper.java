package com.rainydaysengine.rainydays.utils;
import java.util.concurrent.CompletableFuture;

public class CallWrapper {
    // Sync Call
    public static <T> CallResult<T> syncCall(ThrowingSupplier<T> func) {
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
    public static <T> CompletableFuture<CallResult<T>> asyncCall(ThrowingSupplier<T> func) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                T result = func.get();
                return CallResult.success(result); // assuming you have a success factory
            } catch (Exception ex) {
                return CallResult.failure(ex); // assuming you have a failure factory
            }
        });
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Usage:
     *
     * CallWrapper.asyncCall(() -> {
     *     // simulate slow call (e.g., external API)
     *     Thread.sleep(500);
     *     return "Async result";
     * });
     */
}