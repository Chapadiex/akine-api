package com.akine_api.domain.exception;

public class SubscriptionNotFoundException extends DomainException {
    public SubscriptionNotFoundException(String id) {
        super("Suscripción no encontrada: " + id);
    }
}
