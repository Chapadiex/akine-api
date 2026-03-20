package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.PrestacionArancelable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrestacionArancelableRepositoryPort {
    PrestacionArancelable save(PrestacionArancelable prestacion);
    Optional<PrestacionArancelable> findById(UUID id);
    Optional<PrestacionArancelable> findByCodigoInterno(String codigoInterno);
    List<PrestacionArancelable> findAll();
}
