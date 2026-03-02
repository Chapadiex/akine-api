package com.akine_api.domain.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
