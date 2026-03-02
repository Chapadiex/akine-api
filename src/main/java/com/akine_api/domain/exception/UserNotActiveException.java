package com.akine_api.domain.exception;

public class UserNotActiveException extends DomainException {
    public UserNotActiveException() {
        super("La cuenta no está activa. Revisá tu email para activarla.");
    }
}
