package com.akine_api.domain.exception;

public class PlanDowngradeNotAllowedException extends DomainException {

    public PlanDowngradeNotAllowedException(String detail) {
        super("No se puede hacer downgrade al plan seleccionado: " + detail);
    }
}
