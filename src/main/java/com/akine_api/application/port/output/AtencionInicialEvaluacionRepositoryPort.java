package com.akine_api.application.port.output;

import com.akine_api.domain.model.AtencionInicialEvaluacion;

import java.util.Optional;
import java.util.UUID;

public interface AtencionInicialEvaluacionRepositoryPort {
    AtencionInicialEvaluacion save(AtencionInicialEvaluacion evaluacion);
    Optional<AtencionInicialEvaluacion> findByAtencionInicialId(UUID atencionInicialId);
}
