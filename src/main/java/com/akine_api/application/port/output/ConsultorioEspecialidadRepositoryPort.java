package com.akine_api.application.port.output;

import com.akine_api.domain.model.ConsultorioEspecialidad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultorioEspecialidadRepositoryPort {
    ConsultorioEspecialidad save(ConsultorioEspecialidad consultorioEspecialidad);
    Optional<ConsultorioEspecialidad> findByConsultorioIdAndEspecialidadId(UUID consultorioId, UUID especialidadId);
    List<ConsultorioEspecialidad> findByConsultorioId(UUID consultorioId);
    List<ConsultorioEspecialidad> findByConsultorioIdAndActivo(UUID consultorioId, boolean activo);
    List<ConsultorioEspecialidad> findByConsultorioIdAndNombreContaining(UUID consultorioId, String normalizedSearch, boolean includeInactive);
}
