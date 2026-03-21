package com.akine_api.domain.exception;

public class PlanLimitExceededException extends DomainException {

    public PlanLimitExceededException(String recurso, int limite) {
        super("Límite de " + recurso + " alcanzado para el plan actual (máximo: " + limite + ")");
    }
}
