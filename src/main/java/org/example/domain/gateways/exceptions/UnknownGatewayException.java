package org.example.domain.gateways.exceptions;

public class UnknownGatewayException extends RuntimeException {
    public UnknownGatewayException(String message) {
        super(message);
    }
}
