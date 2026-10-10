package com.greentech.exception;

/**
 * Thrown when credentials or session validation fails.
 */
public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message);
    }
}
