package com.akine_api.domain.repository.cobertura;

import com.akine_api.domain.model.cobertura.FinanciadorSalud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinanciadorSaludRepositoryPort {
    FinanciadorSalud save(FinanciadorSalud financiadorSalud);
    FinanciadorSalud update(UUID id, FinanciadorSalud financiadorSalud);
    Optional<FinanciadorSalud> findById(UUID id);
    List<FinanciadorSalud> findAllByConsultorioId(UUID consultorioId);
    boolean existsByNombreAndConsultorioId(String nombre, UUID consultorioId);
    boolean existsByNombreAndConsultorioIdExcludingId(String nombre, UUID consultorioId, UUID excludeId);
}
