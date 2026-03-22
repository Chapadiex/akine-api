package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.domain.model.cobro.EstadoLoteOs;
import com.akine_api.infrastructure.persistence.entity.cobro.LoteFacturacionOsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoteFacturacionOsJpaRepository extends JpaRepository<LoteFacturacionOsEntity, UUID> {

    List<LoteFacturacionOsEntity> findByConsultorioId(UUID consultorioId);

    List<LoteFacturacionOsEntity> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLoteOs estado);

    List<LoteFacturacionOsEntity> findByConsultorioIdAndFinanciadorId(UUID consultorioId, UUID financiadorId);

    @Query("SELECT d.liquidacionSesionId FROM LoteFacturacionOsDetalleEntity d " +
           "JOIN LoteFacturacionOsEntity l ON d.loteId = l.id " +
           "WHERE l.consultorioId = :consultorioId AND l.estado != 'ANULADO'")
    List<UUID> findLiquidacionIdsEnLotesActivos(@Param("consultorioId") UUID consultorioId);
}
