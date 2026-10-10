package com.greentech.util;

import com.greentech.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Server-side input validation utility to sanitize and enforce business constraints on all inputs.
 */
public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private ValidationUtil() {}

    public static void requireNonEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be empty.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        requireNonEmpty(email, "Email address");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validatePassword(String password, int minLength) throws ValidationException {
        requireNonEmpty(password, "Password");
        if (password.length() < minLength) {
            throw new ValidationException("Password must be at least " + minLength + " characters long.");
        }
    }

    public static void validateRole(String role, boolean allowAdmin) throws ValidationException {
        requireNonEmpty(role, "Role");
        String upper = role.trim().toUpperCase();
        if (!allowAdmin && "ADMIN".equals(upper)) {
            throw new ValidationException("Public registration as Administrator is prohibited.");
        }
        if (!"ADMIN".equals(upper) && !"INSTRUCTOR".equals(upper) && !"LEARNER".equals(upper)) {
            throw new ValidationException("Invalid role selected: " + role);
        }
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
