package com.akine_api.application.port.output;

import com.akine_api.domain.model.Profesional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfesionalRepositoryPort {
    Profesional save(Profesional profesional);
    Optional<Profesional> findById(UUID id);
    Optional<Profesional> findByEmail(String email);
    List<Profesional> findByConsultorioId(UUID consultorioId);
    boolean existsByMatriculaAndConsultorioId(String matricula, UUID consultorioId);
    boolean existsByMatriculaAndConsultorioIdAndIdNot(String matricula, UUID consultorioId, UUID excludeId);
}
