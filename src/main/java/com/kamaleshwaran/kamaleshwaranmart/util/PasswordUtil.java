package com.kamaleshwaran.kamaleshwaranmart.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 * Never stores or logs plaintext passwords.
 */
public final class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int LOG_ROUNDS = 10;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Hashes a plaintext password using BCrypt with salt.
     *
     * @param plainPassword plaintext password
     * @return 60-character BCrypt hashed string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies a candidate plaintext password against a stored BCrypt hash.
     *
     * @param plainPassword  plaintext password candidate
     * @param hashedPassword stored hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("Error verifying password hash: {}", e.getMessage());
            return false;
        }
    }
}
