package org.example.domain.gateways.exceptions;

public class ApiKeyInvalidOrExpiredException extends RuntimeException {
    public ApiKeyInvalidOrExpiredException(String message) {
        super(message);
    }
}
