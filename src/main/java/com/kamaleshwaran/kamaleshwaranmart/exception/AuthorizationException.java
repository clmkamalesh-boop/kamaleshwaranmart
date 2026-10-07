package com.kamaleshwaran.kamaleshwaranmart.exception;

/**
 * Thrown when a user tries to access a resource without proper roles or permissions.
 */
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
