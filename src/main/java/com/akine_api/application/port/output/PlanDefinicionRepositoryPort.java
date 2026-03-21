package com.akine_api.application.port.output;

import com.akine_api.domain.model.PlanDefinicion;

import java.util.List;
import java.util.Optional;

public interface PlanDefinicionRepositoryPort {
    Optional<PlanDefinicion> findByCodigo(String codigo);
    List<PlanDefinicion> findAllActivos();
}
