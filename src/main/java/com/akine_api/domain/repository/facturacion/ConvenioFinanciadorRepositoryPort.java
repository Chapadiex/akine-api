package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioFinanciador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioFinanciadorRepositoryPort {
    ConvenioFinanciador save(ConvenioFinanciador convenio);
    Optional<ConvenioFinanciador> findById(UUID id);
    List<ConvenioFinanciador> findByFinanciadorId(UUID financiadorId);
}
