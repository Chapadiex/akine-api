package com.akine_api.domain.exception;

public class CompanyAlreadyExistsException extends DomainException {
    public CompanyAlreadyExistsException(String cuit) {
        super("Ya existe una empresa con el CUIT: " + cuit);
    }
}
