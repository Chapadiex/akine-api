package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.EstadoLoteOs;
import com.akine_api.domain.model.cobro.LoteFacturacionOs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteFacturacionOsRepositoryPort {
    LoteFacturacionOs save(LoteFacturacionOs lote);
    Optional<LoteFacturacionOs> findById(UUID id);
    List<LoteFacturacionOs> findByConsultorioId(UUID consultorioId);
    List<LoteFacturacionOs> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLoteOs estado);
    List<LoteFacturacionOs> findByConsultorioIdAndFinanciadorId(UUID consultorioId, UUID financiadorId);

    /** Returns the IDs of liquidaciones already included in any non-ANULADO lote for this consultorio */
    List<UUID> findLiquidacionIdsEnLotesActivos(UUID consultorioId);
}
