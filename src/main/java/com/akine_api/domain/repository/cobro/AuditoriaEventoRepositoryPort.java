package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.AuditoriaEvento;

import java.util.List;
import java.util.UUID;

public interface AuditoriaEventoRepositoryPort {
    AuditoriaEvento save(AuditoriaEvento evento);
    List<AuditoriaEvento> findByEntidadId(String entidad, UUID entidadId);
}
