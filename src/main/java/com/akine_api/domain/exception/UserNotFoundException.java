package com.akine_api.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String identifier) {
        super("Usuario no encontrado: " + identifier);
    }
}
