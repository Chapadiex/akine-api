package com.akine_api.application.port.output;

import com.akine_api.domain.model.PacienteConsultorio;

import java.util.List;
import java.util.UUID;

public interface PacienteConsultorioRepositoryPort {
    PacienteConsultorio save(PacienteConsultorio pacienteConsultorio);
    boolean existsByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);
    List<UUID> findPacienteIdsByConsultorioIdAndPacienteIds(UUID consultorioId, List<UUID> pacienteIds);
}
