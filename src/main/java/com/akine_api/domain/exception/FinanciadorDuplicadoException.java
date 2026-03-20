package com.akine_api.domain.exception;

public class FinanciadorDuplicadoException extends DomainException {
    public FinanciadorDuplicadoException(String nombre) {
        super("Ya existe un financiador con el nombre \"" + nombre + "\".");
    }
}
