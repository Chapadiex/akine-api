package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionAjuste;

import java.util.List;
import java.util.UUID;

public interface LiquidacionAjusteRepositoryPort {
    LiquidacionAjuste save(LiquidacionAjuste ajuste);
    List<LiquidacionAjuste> findByLiquidacionId(UUID liquidacionId);
}
