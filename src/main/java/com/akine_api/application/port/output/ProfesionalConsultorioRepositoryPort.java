package com.akine_api.application.port.output;

import com.akine_api.domain.model.ProfesionalConsultorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfesionalConsultorioRepositoryPort {
    List<ProfesionalConsultorio> findByConsultorioId(UUID consultorioId);
    List<ProfesionalConsultorio> findByProfesionalId(UUID profesionalId);
    Optional<ProfesionalConsultorio> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
    ProfesionalConsultorio save(ProfesionalConsultorio pc);
    boolean existsByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
}
