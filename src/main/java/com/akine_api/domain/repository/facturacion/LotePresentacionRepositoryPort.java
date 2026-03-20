package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LotePresentacionRepositoryPort {
    LotePresentacion save(LotePresentacion lote);
    Optional<LotePresentacion> findById(UUID id);
    List<LotePresentacion> findByFinanciadorId(UUID financiadorId);
}
