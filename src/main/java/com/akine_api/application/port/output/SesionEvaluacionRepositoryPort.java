package com.akine_api.application.port.output;

import com.akine_api.domain.model.SesionEvaluacion;
import java.util.Optional;
import java.util.UUID;

public interface SesionEvaluacionRepositoryPort {
    SesionEvaluacion save(SesionEvaluacion evaluacion);
    Optional<SesionEvaluacion> findBySesionId(UUID sesionId);
}
