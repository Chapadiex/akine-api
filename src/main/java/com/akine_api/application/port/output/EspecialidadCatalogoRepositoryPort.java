package com.akine_api.application.port.output;

import com.akine_api.domain.model.EspecialidadCatalogo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EspecialidadCatalogoRepositoryPort {
    EspecialidadCatalogo save(EspecialidadCatalogo especialidadCatalogo);
    Optional<EspecialidadCatalogo> findById(UUID id);
    Optional<EspecialidadCatalogo> findBySlug(String slug);
    List<EspecialidadCatalogo> findAll();
    List<EspecialidadCatalogo> findByIds(List<UUID> ids);
}
