package com.akine_api.application.port.output;

import com.akine_api.domain.model.SesionClinica;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SesionClinicaRepositoryPort {
    SesionClinica save(SesionClinica sesionClinica);
    Optional<SesionClinica> findById(UUID id);
    Optional<SesionClinica> findByTurnoId(UUID turnoId);
    List<SesionClinica> findByConsultorioId(UUID consultorioId);
    List<SesionClinica> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);
}
