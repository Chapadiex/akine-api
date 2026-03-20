package com.akine_api.application.port.output;

import com.akine_api.domain.model.AdjuntoClinico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdjuntoClinicoRepositoryPort {
    AdjuntoClinico save(AdjuntoClinico adjuntoClinico);
    Optional<AdjuntoClinico> findById(UUID id);
    List<AdjuntoClinico> findBySesionId(UUID sesionId);
    List<AdjuntoClinico> findBySesionIds(List<UUID> sesionIds);
    List<AdjuntoClinico> findByAtencionInicialId(UUID atencionInicialId);
    List<AdjuntoClinico> findByAtencionInicialIds(List<UUID> atencionInicialIds);
    List<AdjuntoClinico> findByCasoAtencionId(UUID casoAtencionId);
    void deleteById(UUID id);
}
