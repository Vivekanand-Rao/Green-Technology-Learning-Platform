package com.greentech.exception;

/**
 * Thrown when an authenticated user attempts an operation outside their authorized role.
 */
public class AuthorizationException extends AppException {
    public AuthorizationException(String message) {
        super(message);
    }
}
