package com.akine_api.application.port.output;

import com.akine_api.domain.model.SesionExamenFisico;
import java.util.Optional;
import java.util.UUID;

public interface SesionExamenFisicoRepositoryPort {
    SesionExamenFisico save(SesionExamenFisico examenFisico);
    Optional<SesionExamenFisico> findBySesionId(UUID sesionId);
}
