package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.CobroPaciente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CobroPacienteRepositoryPort {
    CobroPaciente save(CobroPaciente cobro);
    Optional<CobroPaciente> findById(UUID id);
    List<CobroPaciente> findByCajaDiariaId(UUID cajaDiariaId);
    List<CobroPaciente> findByPacienteId(UUID pacienteId, UUID consultorioId);
    List<CobroPaciente> findBySesionId(UUID sesionId);
}
