package com.akine_api.domain.repository.convenio;

import com.akine_api.domain.model.convenio.Prestacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrestacionRepositoryPort {
    Prestacion save(Prestacion p);
    Optional<Prestacion> findById(UUID id);
    Optional<Prestacion> findByCodigoNomenclador(String codigo);
    List<Prestacion> findAll();
    List<Prestacion> findAllActivas();
}
