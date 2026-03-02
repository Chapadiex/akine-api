package com.akine_api.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
