package com.akine_api.application.port.output;

import com.akine_api.domain.model.Paciente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteRepositoryPort {
    Paciente save(Paciente paciente);
    Optional<Paciente> findById(UUID id);
    Optional<Paciente> findByDni(String dni);
    Optional<Paciente> findByUserId(UUID userId);
    List<Paciente> findByIds(List<UUID> ids);
    List<Paciente> searchByNombreApellido(String query, int limit);
}
