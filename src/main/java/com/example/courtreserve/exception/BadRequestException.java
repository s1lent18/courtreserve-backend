package com.example.courtreserve.exception;

/**
 * Exception thrown when a request contains invalid data or violates business rules.
 * This will result in a 400 BAD_REQUEST HTTP response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

