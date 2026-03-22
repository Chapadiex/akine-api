package com.akine_api.domain.repository.sesion;

import com.akine_api.domain.model.sesion.SesionAdministrativa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SesionAdministrativaRepositoryPort {
    SesionAdministrativa save(SesionAdministrativa sesion);
    Optional<SesionAdministrativa> findById(UUID id);
    Optional<SesionAdministrativa> findBySesionId(UUID sesionId);
    Optional<SesionAdministrativa> findByTurnoId(UUID turnoId);
    List<SesionAdministrativa> findByConsultorioId(UUID consultorioId);
}
