package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionFinanciador;

import java.util.Optional;
import java.util.UUID;

public interface LiquidacionFinanciadorRepositoryPort {
    LiquidacionFinanciador save(LiquidacionFinanciador liquidacion);
    Optional<LiquidacionFinanciador> findById(UUID id);
}
