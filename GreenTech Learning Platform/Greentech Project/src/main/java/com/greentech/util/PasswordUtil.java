package com.greentech.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for cryptographic password hashing and verification using salted SHA-256.
 * Supports salt generation, secure comparison, and legacy plain-text fallback for pre-seeded demo records.
 */
public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /**
     * Generates a cryptographically strong 16-byte random salt, Base64-encoded.
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Hashes the given raw password combined with a cryptographic salt using SHA-256.
     */
    public static String hashPassword(String rawPassword, String salt) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        String effectiveSalt = (salt != null) ? salt : "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(effectiveSalt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available in runtime", e);
        }
    }

    /**
     * Verifies a raw password against the stored hash and salt.
     * Also allows plain-text match as backward-compatibility fallback for initial mock/demo accounts.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        // Direct match fallback for legacy pre-seeded database accounts (e.g., admin123)
        if (rawPassword.equals(storedHash)) {
            return true;
        }
        String calculated = hashPassword(rawPassword, storedSalt);
        return MessageDigest.isEqual(calculated.getBytes(StandardCharsets.UTF_8), storedHash.getBytes(StandardCharsets.UTF_8));
    }
}
