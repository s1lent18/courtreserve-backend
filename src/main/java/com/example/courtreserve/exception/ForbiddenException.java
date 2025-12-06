package com.example.courtreserve.exception;

/**
 * Exception thrown when a user doesn't have permission to access a resource.
 * This will result in a 403 FORBIDDEN HTTP response.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

