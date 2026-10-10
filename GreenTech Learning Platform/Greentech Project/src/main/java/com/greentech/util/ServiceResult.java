package com.greentech.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic response container demonstrating Java Generics.
 * Encapsulates operation status, payload data of type T, and optional diagnostic messages.
 *
 * @param <T> the type of payload data
 */
public class ServiceResult<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final List<String> errors;

    public ServiceResult(boolean success, String message, T data) {
        this(success, message, data, Collections.emptyList());
    }

    public ServiceResult(boolean success, String message, T data, List<String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors != null ? new ArrayList<>(errors) : Collections.emptyList();
    }

    public static <T> ServiceResult<T> ok(String message, T data) {
        return new ServiceResult<>(true, message, data);
    }

    public static <T> ServiceResult<T> ok(T data) {
        return new ServiceResult<>(true, "Operation successful", data);
    }

    public static <T> ServiceResult<T> fail(String message) {
        return new ServiceResult<>(false, message, null, Collections.singletonList(message));
    }

    public static <T> ServiceResult<T> fail(String message, List<String> errors) {
        return new ServiceResult<>(false, message, null, errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
