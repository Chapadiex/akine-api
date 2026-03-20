package com.akine_api.domain.repository.cobertura;

import com.akine_api.domain.model.cobertura.PacienteCobertura;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteCoberturaRepositoryPort {
    PacienteCobertura save(PacienteCobertura pacienteCobertura);
    Optional<PacienteCobertura> findById(UUID id);
    List<PacienteCobertura> findByPacienteId(UUID pacienteId);
}
