package com.greentech.exception;

/**
 * Thrown when client or form input fails server-side validation rules.
 */
public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message);
    }
}
