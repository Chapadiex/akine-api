package com.akine_api.application.port.output;

import com.akine_api.domain.model.DiagnosticoClinico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiagnosticoClinicoRepositoryPort {
    DiagnosticoClinico save(DiagnosticoClinico diagnosticoClinico);
    Optional<DiagnosticoClinico> findById(UUID id);
    Optional<DiagnosticoClinico> findByCasoAtencionId(UUID casoAtencionId);
    List<DiagnosticoClinico> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);
}
