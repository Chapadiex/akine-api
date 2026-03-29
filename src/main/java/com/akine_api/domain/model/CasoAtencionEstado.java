package com.akine_api.domain.model;

import com.akine_api.domain.exception.TransicionEstadoInvalidaException;

import java.util.Set;

public enum CasoAtencionEstado {
    BORRADOR,
    EN_EVALUACION,
    ACTIVO,
    EN_TRATAMIENTO,
    EN_PAUSA,
    CERRADO_ALTA,
    CERRADO_ABANDONO,
    CERRADO_DERIVACION;

    public boolean canTransitionTo(CasoAtencionEstado target) {
        return allowedTransitions().contains(target);
    }

    public void validateTransition(CasoAtencionEstado target) {
        if (!canTransitionTo(target)) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede pasar el caso de estado " + this + " a " + target);
        }
    }

    public boolean isCerrado() {
        return this == CERRADO_ALTA || this == CERRADO_ABANDONO || this == CERRADO_DERIVACION;
    }

    private Set<CasoAtencionEstado> allowedTransitions() {
        return switch (this) {
            case BORRADOR       -> Set.of(EN_EVALUACION, ACTIVO, CERRADO_ALTA, CERRADO_ABANDONO, CERRADO_DERIVACION);
            case EN_EVALUACION  -> Set.of(ACTIVO, CERRADO_ABANDONO, CERRADO_DERIVACION);
            case ACTIVO         -> Set.of(EN_TRATAMIENTO, EN_PAUSA, CERRADO_ALTA, CERRADO_ABANDONO, CERRADO_DERIVACION);
            case EN_TRATAMIENTO -> Set.of(EN_PAUSA, CERRADO_ALTA, CERRADO_ABANDONO, CERRADO_DERIVACION);
            case EN_PAUSA       -> Set.of(EN_TRATAMIENTO, CERRADO_ALTA, CERRADO_ABANDONO, CERRADO_DERIVACION);
            case CERRADO_ALTA, CERRADO_ABANDONO, CERRADO_DERIVACION -> Set.of();
        };
    }
}
