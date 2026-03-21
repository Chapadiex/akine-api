package com.akine_api.domain.exception;

public class FeatureNotAvailableException extends DomainException {

    public FeatureNotAvailableException(String feature, String planNombre) {
        super("La funcionalidad '" + feature + "' no está disponible en el plan " + planNombre);
    }
}
