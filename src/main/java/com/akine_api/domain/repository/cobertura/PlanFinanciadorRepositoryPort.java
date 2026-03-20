package com.akine_api.domain.repository.cobertura;

import com.akine_api.domain.model.cobertura.PlanFinanciador;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanFinanciadorRepositoryPort {
    PlanFinanciador save(PlanFinanciador planFinanciador);
    PlanFinanciador update(UUID id, PlanFinanciador planFinanciador);
    Optional<PlanFinanciador> findById(UUID id);
    List<PlanFinanciador> findByFinanciadorId(UUID financiadorId);
    boolean existsWithOverlappingVigencia(String nombrePlan, UUID financiadorId, LocalDate vigenciaDesde, LocalDate vigenciaHasta);
    boolean existsWithOverlappingVigenciaExcludingId(String nombrePlan, UUID financiadorId, LocalDate vigenciaDesde, LocalDate vigenciaHasta, UUID excludeId);
}
