package com.akine_api.application.port.output;

import com.akine_api.domain.model.PlanTerapeutico;

import java.util.Optional;
import java.util.UUID;

public interface PlanTerapeuticoRepositoryPort {
    PlanTerapeutico save(PlanTerapeutico planTerapeutico);
    Optional<PlanTerapeutico> findLatestByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
    Optional<PlanTerapeutico> findByAtencionInicialId(UUID atencionInicialId);
}
