package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiquidacionSesionRepositoryPort {
    LiquidacionSesion save(LiquidacionSesion liquidacion);
    Optional<LiquidacionSesion> findById(UUID id);
    Optional<LiquidacionSesion> findActivaBySesionId(UUID sesionId);
    List<LiquidacionSesion> findByConsultorioId(UUID consultorioId);
    List<LiquidacionSesion> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLiquidacion estado);
}
