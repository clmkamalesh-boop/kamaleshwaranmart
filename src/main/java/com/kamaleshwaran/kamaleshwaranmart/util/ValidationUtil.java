package com.kamaleshwaran.kamaleshwaranmart.util;

import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validates user input across authentication, products, carts, and reviews.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    private ValidationUtil() {
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Please provide a valid email address.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasUpper || !hasLower || !hasDigit) {
            throw new ValidationException("Password must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }

    public static void validateRequiredString(String value, String fieldName, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
        if (value.trim().length() > maxLength) {
            throw new ValidationException(fieldName + " cannot exceed " + maxLength + " characters.");
        }
    }

    public static void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new ValidationException("Price is required.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than zero.");
        }
    }

    public static void validateStock(int stock) {
        if (stock < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }
    }

    public static void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5 stars.");
        }
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
}
