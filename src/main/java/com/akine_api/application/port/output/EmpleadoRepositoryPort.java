package com.akine_api.application.port.output;

import com.akine_api.domain.model.Empleado;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpleadoRepositoryPort {
    Empleado save(Empleado empleado);
    Optional<Empleado> findByConsultorioIdAndId(UUID consultorioId, UUID id);
    Optional<Empleado> findByUserId(UUID userId);
    List<Empleado> findByConsultorioId(UUID consultorioId);
    boolean existsByConsultorioIdAndEmail(UUID consultorioId, String email);
    boolean existsByConsultorioIdAndEmailAndIdNot(UUID consultorioId, String email, UUID id);
    boolean existsByConsultorioIdAndDniAndIdNot(UUID consultorioId, String dni, UUID id);
}
