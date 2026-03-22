package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.infrastructure.persistence.entity.cobro.LiquidacionSesionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiquidacionSesionJpaRepository extends JpaRepository<LiquidacionSesionEntity, UUID> {

    @Query("SELECT l FROM LiquidacionSesionEntity l WHERE l.sesionId = :sesionId AND l.estado != 'ANULADA'")
    Optional<LiquidacionSesionEntity> findActivaBySesionId(@Param("sesionId") UUID sesionId);

    List<LiquidacionSesionEntity> findByConsultorioId(UUID consultorioId);

    List<LiquidacionSesionEntity> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLiquidacion estado);

    @Query("SELECT l FROM LiquidacionSesionEntity l " +
           "WHERE l.consultorioId = :consultorioId AND l.financiadorId = :financiadorId " +
           "AND l.esFacturableOs = true AND l.estado IN ('LIQUIDADA_OS', 'LIQUIDADA_MIXTA')")
    List<LiquidacionSesionEntity> findFacturablesByConsultorioAndFinanciador(
            @Param("consultorioId") UUID consultorioId,
            @Param("financiadorId") UUID financiadorId);
}
