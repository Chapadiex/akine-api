package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioFinanciadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConvenioFinanciadorJpaRepository extends JpaRepository<ConvenioFinanciadorEntity, UUID> {

    List<ConvenioFinanciadorEntity> findByFinanciadorId(UUID financiadorId);

    List<ConvenioFinanciadorEntity> findByConsultorioId(UUID consultorioId);

    @Query("""
            SELECT c FROM ConvenioFinanciadorEntity c
            WHERE c.financiador.id = :financiadorId
              AND (:planId IS NULL OR c.plan.id = :planId)
              AND c.consultorioId = :consultorioId
              AND c.activo = TRUE
              AND c.vigenciaDesde <= :fecha
              AND (c.vigenciaHasta IS NULL OR c.vigenciaHasta >= :fecha)
            ORDER BY c.vigenciaDesde DESC
            """)
    Optional<ConvenioFinanciadorEntity> findVigenteByFinanciadorPlanConsultorio(
            @Param("financiadorId") UUID financiadorId,
            @Param("planId") UUID planId,
            @Param("consultorioId") UUID consultorioId,
            @Param("fecha") LocalDate fecha);

    @Query("""
            SELECT COUNT(c) > 0 FROM ConvenioFinanciadorEntity c
            WHERE c.financiador.id = :financiadorId
              AND (:planId IS NULL OR c.plan.id = :planId)
              AND c.consultorioId = :consultorioId
              AND c.activo = TRUE
              AND (:excludeId IS NULL OR c.id <> :excludeId)
              AND c.vigenciaDesde <= COALESCE(:vigenciaHasta, CURRENT_DATE)
              AND (c.vigenciaHasta IS NULL OR c.vigenciaHasta >= :vigenciaDesde)
            """)
    boolean existsSolapamiento(
            @Param("financiadorId") UUID financiadorId,
            @Param("planId") UUID planId,
            @Param("consultorioId") UUID consultorioId,
            @Param("vigenciaDesde") LocalDate vigenciaDesde,
            @Param("vigenciaHasta") LocalDate vigenciaHasta,
            @Param("excludeId") UUID excludeId);
}
