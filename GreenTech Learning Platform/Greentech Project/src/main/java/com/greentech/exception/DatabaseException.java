package com.greentech.exception;

/**
 * Thrown when low-level JDBC or database execution errors occur, shielding callers from raw SQL errors.
 */
public class DatabaseException extends AppException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
