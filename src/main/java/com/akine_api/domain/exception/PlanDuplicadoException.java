package com.akine_api.domain.exception;

public class PlanDuplicadoException extends DomainException {
    public PlanDuplicadoException(String nombrePlan) {
        super("Ya existe un plan \"" + nombrePlan + "\" con vigencia que se superpone. Ajustá las fechas de vigencia para que no se solapen.");
    }
}
