package com.akine_api.domain.model;

import java.util.Set;

public enum TurnoEstado {
    PROGRAMADO,
    CONFIRMADO,
    EN_ESPERA,
    EN_CURSO,
    COMPLETADO,
    CANCELADO,
    AUSENTE;

    public boolean canTransitionTo(TurnoEstado target) {
        return allowedTransitions().contains(target);
    }

    private Set<TurnoEstado> allowedTransitions() {
        return switch (this) {
            case PROGRAMADO -> Set.of(CONFIRMADO, CANCELADO, AUSENTE);
            case CONFIRMADO -> Set.of(EN_ESPERA, EN_CURSO, CANCELADO, AUSENTE);
            case EN_ESPERA  -> Set.of(EN_CURSO, CANCELADO, AUSENTE);
            case EN_CURSO   -> Set.of(COMPLETADO);
            case COMPLETADO, CANCELADO, AUSENTE -> Set.of();
        };
    }
}
