package com.akine_api.domain.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String email) {
        super("Ya existe un usuario con el email: " + email);
    }
}
