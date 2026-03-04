package com.akine_api.domain.exception;

public class SubscriptionNotActiveException extends DomainException {
    public SubscriptionNotActiveException() {
        super("No hay una suscripción activa y vigente para tu consultorio.");
    }
}
