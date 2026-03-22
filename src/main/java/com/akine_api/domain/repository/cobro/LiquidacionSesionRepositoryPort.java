package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.model.cobro.TipoLiquidacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiquidacionSesionRepositoryPort {
    LiquidacionSesion save(LiquidacionSesion liquidacion);
    Optional<LiquidacionSesion> findById(UUID id);
    Optional<LiquidacionSesion> findActivaBySesionId(UUID sesionId);
    List<LiquidacionSesion> findByConsultorioId(UUID consultorioId);
    List<LiquidacionSesion> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLiquidacion estado);

    /**
     * Returns all facturable-OS liquidaciones for a consultorio/financiador
     * whose sesion falls within the given YYYY-MM period, regardless of lote membership.
     * Service layer is responsible for filtering out those already in active lotes.
     */
    List<LiquidacionSesion> findFacturablesByConsultorioAndFinanciador(UUID consultorioId, UUID financiadorId);

    /** Used by reporting: all liquidaciones of a given tipo for a consultorio. */
    List<LiquidacionSesion> findByConsultorioIdAndTipoLiquidacion(UUID consultorioId, TipoLiquidacion tipo);
}
