package com.kamaleshwaran.kamaleshwaranmart.exception;

/**
 * Thrown when credentials fail or authentication is rejected.
 */
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
