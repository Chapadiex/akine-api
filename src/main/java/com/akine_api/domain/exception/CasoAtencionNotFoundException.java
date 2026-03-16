package com.akine_api.domain.exception;

import java.util.UUID;

public class CasoAtencionNotFoundException extends DomainException {

    public CasoAtencionNotFoundException(UUID id) {
        super("Caso de atención no encontrado: " + id);
    }

    public CasoAtencionNotFoundException(String message) {
        super(message);
    }
}
