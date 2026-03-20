package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioPrestacionValorRepositoryPort {
    ConvenioPrestacionValor save(ConvenioPrestacionValor valor);
    Optional<ConvenioPrestacionValor> findById(UUID id);
    List<ConvenioPrestacionValor> findByConvenioId(UUID convenioId);
}
