package com.akine_api.infrastructure.persistence.repository.cobertura;

import com.akine_api.infrastructure.persistence.entity.cobertura.PlanFinanciadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlanFinanciadorJpaRepository extends JpaRepository<PlanFinanciadorEntity, UUID> {
    List<PlanFinanciadorEntity> findByFinanciadorId(UUID financiadorId);

    @Query("SELECT COUNT(p) > 0 FROM PlanFinanciadorEntity p WHERE " +
            "LOWER(p.nombrePlan) = LOWER(:nombrePlan) AND " +
            "p.financiador.id = :financiadorId AND " +
            "(:vigenciaHasta IS NULL OR p.vigenciaDesde IS NULL OR p.vigenciaDesde <= :vigenciaHasta) AND " +
            "(:vigenciaDesde IS NULL OR p.vigenciaHasta IS NULL OR p.vigenciaHasta >= :vigenciaDesde)")
    boolean existsWithOverlappingVigencia(
            @Param("nombrePlan") String nombrePlan,
            @Param("financiadorId") UUID financiadorId,
            @Param("vigenciaDesde") LocalDate vigenciaDesde,
            @Param("vigenciaHasta") LocalDate vigenciaHasta);

    @Query("SELECT COUNT(p) > 0 FROM PlanFinanciadorEntity p WHERE " +
            "LOWER(p.nombrePlan) = LOWER(:nombrePlan) AND " +
            "p.financiador.id = :financiadorId AND " +
            "p.id != :excludeId AND " +
            "(:vigenciaHasta IS NULL OR p.vigenciaDesde IS NULL OR p.vigenciaDesde <= :vigenciaHasta) AND " +
            "(:vigenciaDesde IS NULL OR p.vigenciaHasta IS NULL OR p.vigenciaHasta >= :vigenciaDesde)")
    boolean existsWithOverlappingVigenciaExcludingId(
            @Param("nombrePlan") String nombrePlan,
            @Param("financiadorId") UUID financiadorId,
            @Param("vigenciaDesde") LocalDate vigenciaDesde,
            @Param("vigenciaHasta") LocalDate vigenciaHasta,
            @Param("excludeId") UUID excludeId);
}
