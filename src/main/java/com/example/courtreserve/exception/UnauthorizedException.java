package com.example.courtreserve.exception;

/**
 * Exception thrown when authentication fails or credentials are invalid.
 * This will result in a 401 UNAUTHORIZED HTTP response.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

